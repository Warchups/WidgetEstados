package com.gnommostudios.widgetestados

import android.content.*
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var statePreferences: SharedPreferences? = null

    private var stateImage: ImageView? = null
    private var state: String? = null

    private var stateButton1: ImageButton? = null
    private var stateButton2: ImageButton? = null
    private var stateButton3: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statePreferences = getSharedPreferences("states", Context.MODE_PRIVATE)

        stateImage = findViewById(R.id.state_image)

        stateButton1 = findViewById(R.id.button_state_1)
        stateButton2 = findViewById(R.id.button_state_2)
        stateButton3 = findViewById(R.id.button_state_3)

        stateButton1!!.setOnClickListener(this)
        stateButton2!!.setOnClickListener(this)
        stateButton3!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        changeButtons()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_state_1 -> state = "call"
            R.id.button_state_2 -> state = "end"
            R.id.button_state_3 -> state = "cancel"
        }

        val editor = statePreferences!!.edit()

        editor.putString("state", state)

        editor.commit()

        changeButtons()
    }

    private fun changeButtons() {
        state = statePreferences!!.getString("state", "end")

        when (state) {
            "call" -> {
                stateImage!!.setImageResource(R.drawable.baseline_call_black_18dp)

                stateButton1!!.setImageResource(R.drawable.baseline_call_black_18dp)
                stateButton2!!.setImageResource(R.drawable.outline_call_end_black_18dp)
                stateButton3!!.setImageResource(R.drawable.outline_cancel_black_18dp)

                stateButton1!!.isEnabled = false
                stateButton2!!.isEnabled = true
                stateButton3!!.isEnabled = true
            }
            "end" -> {
                stateImage!!.setImageResource(R.drawable.baseline_call_end_black_18dp)

                stateButton1!!.setImageResource(R.drawable.outline_call_black_18dp)
                stateButton2!!.setImageResource(R.drawable.baseline_call_end_black_18dp)
                stateButton3!!.setImageResource(R.drawable.outline_cancel_black_18dp)

                stateButton1!!.isEnabled = true
                stateButton2!!.isEnabled = false
                stateButton3!!.isEnabled = true
            }
            "cancel" -> {
                stateImage!!.setImageResource(R.drawable.baseline_cancel_black_18dp)

                stateButton1!!.setImageResource(R.drawable.outline_call_black_18dp)
                stateButton2!!.setImageResource(R.drawable.outline_call_end_black_18dp)
                stateButton3!!.setImageResource(R.drawable.baseline_cancel_black_18dp)

                stateButton1!!.isEnabled = true
                stateButton2!!.isEnabled = true
                stateButton3!!.isEnabled = false
            }
        }
    }
}
