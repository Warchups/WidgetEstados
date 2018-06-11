package com.gnommostudios.widgetestados.views

import android.Manifest
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gnommostudios.widgetestados.R
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import com.gnommostudios.widgetestados.service.PermissionUtils
import com.gnommostudios.widgetestados.service.TelephonyService
import com.gnommostudios.widgetestados.utils.MyPhoneStates
import com.gnommostudios.widgetestados.views.widgets.ChangeStateWidget
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val PERMISSION_REQUEST = 100
    }

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras!!.getBoolean("UPDATE")) {
                changeButtons()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statePreferences = getSharedPreferences("states", Context.MODE_PRIVATE)

        buttonState1.setOnClickListener(this)
        buttonState2.setOnClickListener(this)
        buttonState3.setOnClickListener(this)

        registerReceiver(mMessageReceiver, IntentFilter("MainActivity"))

        checkPermissions()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonState1 -> state = MyPhoneStates.TALKING
            R.id.buttonState2 -> state = MyPhoneStates.IDLE
            R.id.buttonState3 -> state = MyPhoneStates.BLOCK
        }

        val editor = statePreferences!!.edit()

        editor.putString("state", state)

        editor.apply()

        changeButtons()
    }

    //--------------------------------------------------
    // Permissions
    //--------------------------------------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                isPermissionGranted(grantResults)
                return
            }
        }
    }

    private fun changeButtons() {
        state = statePreferences!!.getString("state", MyPhoneStates.IDLE)

        val intent = Intent(this, ChangeStateWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(application, ChangeStateWidget::class.java))

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)

        when (state) {
            MyPhoneStates.TALKING -> {
                stateImage.setImageResource(R.drawable.baseline_call_black_18dp)

                buttonState1.setImageResource(R.drawable.baseline_call_black_18dp)
                buttonState2.setImageResource(R.drawable.outline_call_end_black_18dp)
                buttonState3.setImageResource(R.drawable.outline_cancel_black_18dp)

                buttonState1.isEnabled = false
                buttonState2.isEnabled = true
                buttonState3.isEnabled = true
            }
            MyPhoneStates.IDLE -> {
                stateImage.setImageResource(R.drawable.baseline_call_end_black_18dp)

                buttonState1.setImageResource(R.drawable.outline_call_black_18dp)
                buttonState2.setImageResource(R.drawable.baseline_call_end_black_18dp)
                buttonState3.setImageResource(R.drawable.outline_cancel_black_18dp)

                buttonState1.isEnabled = true
                buttonState2.isEnabled = false
                buttonState3.isEnabled = true
            }
            MyPhoneStates.BLOCK -> {
                stateImage.setImageResource(R.drawable.baseline_cancel_black_18dp)

                buttonState1.setImageResource(R.drawable.outline_call_black_18dp)
                buttonState2.setImageResource(R.drawable.outline_call_end_black_18dp)
                buttonState3.setImageResource(R.drawable.baseline_cancel_black_18dp)

                buttonState1.isEnabled = true
                buttonState2.isEnabled = true
                buttonState3.isEnabled = false
            }
        }
    }

    private fun checkPermissions() {
        // Checks the Android version of the device.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val canWriteExternalStorage = PermissionUtils.canReadPhoneState(this)
            val canReadExternalStorage = PermissionUtils.canAccessCoarseLocation(this)
            if (!canWriteExternalStorage || !canReadExternalStorage) {
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST)
            } else {
                // Permission was granted.
                callPhoneManager()
            }
        } else {
            // Version is below Marshmallow.
            callPhoneManager()
        }
    }

    private fun callPhoneManager() {
        val serviceIntent = Intent(this, TelephonyService::class.java)
        //serviceIntent.action = "com.gnommostudios.widgetestados.service.TelephonyService"
        startService(serviceIntent)
    }

    private fun isPermissionGranted(grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                callPhoneManager()
            } else {
                PermissionUtils.alertAndFinish(this)
            }
        }
    }
}
