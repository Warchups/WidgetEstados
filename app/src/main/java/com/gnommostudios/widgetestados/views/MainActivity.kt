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

        buttonTalking.setOnClickListener(this)
        buttonIdle.setOnClickListener(this)
        buttonLocked.setOnClickListener(this)

        registerReceiver(mMessageReceiver, IntentFilter("MainActivity"))
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonTalking -> state = MyPhoneStates.TALKING
            R.id.buttonIdle -> state = MyPhoneStates.IDLE
            R.id.buttonLocked -> state = MyPhoneStates.LOCKED
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

                buttonTalking.setImageResource(R.drawable.baseline_call_black_18dp)
                buttonIdle.setImageResource(R.drawable.outline_call_end_black_18dp)
                buttonLocked.setImageResource(R.drawable.outline_cancel_black_18dp)

                buttonTalking.isEnabled = false
                buttonIdle.isEnabled = true
                buttonLocked.isEnabled = true
            }
            MyPhoneStates.IDLE -> {
                stateImage.setImageResource(R.drawable.baseline_call_end_black_18dp)

                buttonTalking.setImageResource(R.drawable.outline_call_black_18dp)
                buttonIdle.setImageResource(R.drawable.baseline_call_end_black_18dp)
                buttonLocked.setImageResource(R.drawable.outline_cancel_black_18dp)

                buttonTalking.isEnabled = true
                buttonIdle.isEnabled = false
                buttonLocked.isEnabled = true
            }
            MyPhoneStates.LOCKED -> {
                stateImage.setImageResource(R.drawable.baseline_cancel_black_18dp)

                buttonTalking.setImageResource(R.drawable.outline_call_black_18dp)
                buttonIdle.setImageResource(R.drawable.outline_call_end_black_18dp)
                buttonLocked.setImageResource(R.drawable.baseline_cancel_black_18dp)

                buttonTalking.isEnabled = true
                buttonIdle.isEnabled = true
                buttonLocked.isEnabled = false
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
