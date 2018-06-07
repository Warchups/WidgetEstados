package com.gnommostudios.widgetestados

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var statePreferences: SharedPreferences? = null

    private var stateTxt: TextView? = null
    private var state: String? = null

    private var stateButton1: Button? = null
    private var stateButton2: Button? = null
    private var stateButton3: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statePreferences = getSharedPreferences("states", Context.MODE_PRIVATE)

        stateTxt = findViewById(R.id.state_txt)

        stateButton1 = findViewById(R.id.button_state_1)
        stateButton2 = findViewById(R.id.button_state_2)
        stateButton3 = findViewById(R.id.button_state_3)

        stateButton1!!.setOnClickListener(this)
        stateButton2!!.setOnClickListener(this)
        stateButton3!!.setOnClickListener(this)

        state = statePreferences!!.getString("state", "")

        stateTxt!!.text = state

        changeButtons()
    }

    override fun onClick(v: View?) {

        Log.i("Prueba", "pene")

        when (v!!.id) {
            R.id.button_state_1 -> state = ":)"
            R.id.button_state_2 -> state = ":("
            R.id.button_state_3 -> state = ":D"
        }

        val editor = statePreferences!!.edit()

        editor.putString("state", state)

        editor.commit()

        stateTxt!!.text = state

        changeButtons()
    }

    private fun changeButtons() {
        when (state) {
            ":)" -> {
                stateButton1!!.isEnabled = false
                stateButton2!!.isEnabled = true
                stateButton3!!.isEnabled = true
            }
            ":(" -> {
                stateButton1!!.isEnabled = true
                stateButton2!!.isEnabled = false
                stateButton3!!.isEnabled = true
            }
            ":D" -> {
                stateButton1!!.isEnabled = true
                stateButton2!!.isEnabled = true
                stateButton3!!.isEnabled = false
            }
            else -> {
                stateButton1!!.isEnabled = true
                stateButton2!!.isEnabled = true
                stateButton3!!.isEnabled = true
            }
        }
    }
}
