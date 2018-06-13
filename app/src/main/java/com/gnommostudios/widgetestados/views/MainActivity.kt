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
import com.gnommostudios.widgetestados.views.widgets.ListTestWidget
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    private var telephonyManager: TelephonyManager? = null

    private val mapPhones = HashMap<String, String>()
    private val randomNames = arrayListOf("Jorge", "Quique", "Borja", "Xito", "Manu", "Cristian", "Alberto", "Mario", "Raul")

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

        addPhone.setOnClickListener({
            createRandomPhone()
        })

        viewListButton.setOnClickListener({
            val intent = Intent(this, PhonesListActivity::class.java)

            startActivity(intent)
        })

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        state = statePreferences!!.getString("state", MyPhoneStates.IDLE)

        if (telephonyManager!!.callState == TelephonyManager.CALL_STATE_IDLE && state!! != MyPhoneStates.LOCKED) {
            state = MyPhoneStates.IDLE

            val editor = statePreferences!!.edit()

            editor.putString("state", state)

            editor.apply()
        }

        registerReceiver(mMessageReceiver, IntentFilter("MainActivity"))

        mapPhones["Christian"] = "610938758"
        mapPhones["GnommoOfi"] = "962064094"

        updatePhonesList()

        changeButtons()
    }

    private fun createRandomPhone() {
        var number = "6"

        val r = Random()
        for (i in 0..7) {
            number += r.nextInt(9)
        }

        val name = randomNames[r.nextInt(randomNames.size)]

        //Log.i(name, number)

        mapPhones[name] = number

        updatePhonesList()
    }

    private fun updatePhonesList() {
        val listPhonesPrefs = getSharedPreferences("phones", Context.MODE_PRIVATE)
        val listPhonesPrefsEditor = listPhonesPrefs.edit()

        for (s in mapPhones.keys) {
            listPhonesPrefsEditor.putString("phone_$s", mapPhones[s])
        }

        listPhonesPrefsEditor.apply()

        val intent = Intent(this, ListTestWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(application, ListTestWidget::class.java))

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
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
