package com.gnommostudios.widgetestados.views

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gnommostudios.widgetestados.R
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.ComponentName
import com.gnommostudios.widgetestados.utils.MyPhoneStates
import com.gnommostudios.widgetestados.views.widgets.ChangeStateWidget
import kotlinx.android.synthetic.main.activity_main.*
import android.content.SharedPreferences
import android.telephony.TelephonyManager

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    private var telephonyManager: TelephonyManager? = null

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

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        state = statePreferences!!.getString("state", MyPhoneStates.IDLE)

        if (telephonyManager!!.callState == TelephonyManager.CALL_STATE_IDLE && state!! != MyPhoneStates.LOCKED) {
            state = MyPhoneStates.IDLE

            val editor = statePreferences!!.edit()

            editor.putString("state", state)

            editor.apply()
        }

        registerReceiver(mMessageReceiver, IntentFilter("MainActivity"))

        changeButtons()
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
                stateImage.setImageResource(R.drawable.baseline_call_24)

                buttonTalking.setImageResource(R.drawable.baseline_call_24)
                buttonIdle.setImageResource(R.drawable.outline_call_end_24)
                buttonLocked.setImageResource(R.drawable.outline_cancel_24)

                buttonTalking.isEnabled = false
                buttonIdle.isEnabled = true
                buttonLocked.isEnabled = true
            }
            MyPhoneStates.IDLE -> {
                stateImage.setImageResource(R.drawable.baseline_call_end_24)

                buttonTalking.setImageResource(R.drawable.outline_call_24)
                buttonIdle.setImageResource(R.drawable.baseline_call_end_24)
                buttonLocked.setImageResource(R.drawable.outline_cancel_24)

                buttonTalking.isEnabled = true
                buttonIdle.isEnabled = false
                buttonLocked.isEnabled = true
            }
            MyPhoneStates.LOCKED -> {
                stateImage.setImageResource(R.drawable.baseline_cancel_24)

                buttonTalking.setImageResource(R.drawable.outline_call_24)
                buttonIdle.setImageResource(R.drawable.outline_call_end_24)
                buttonLocked.setImageResource(R.drawable.baseline_cancel_24)

                buttonTalking.isEnabled = true
                buttonIdle.isEnabled = true
                buttonLocked.isEnabled = false
            }
        }
    }

}
