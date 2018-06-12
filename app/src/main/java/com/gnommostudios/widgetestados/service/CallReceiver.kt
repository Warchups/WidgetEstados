package com.gnommostudios.widgetestados.service

import android.content.Context
import android.util.Log
import com.gnommostudios.widgetestados.utils.MyPhoneStates

class CallReceiver : PhoneCallReceiver() {

    companion object {
        private const val TAG = "CALLRECEIVER"
    }

    override fun onIncomingCallStarted(context: Context, number: String?) {
        //Toast.makeText(context, "onIncomingCallStarted", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onIncomingCallStarted, number: $number")

        changeState(context, MyPhoneStates.TALKING)
    }

    override fun onOutgoingCallStarted(context: Context, number: String?) {
        //Toast.makeText(context, "onOutgoingCallStarted", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onOutgoingCallStarted, number: $number")

        changeState(context, MyPhoneStates.TALKING)
    }

    override fun onIncomingCallEnded(context: Context, number: String?) {
        //Toast.makeText(context, "onIncomingCallEnded", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onIncomingCallEnded, number: $number")

        changeState(context, MyPhoneStates.IDLE)
    }

    override fun onOutgoingCallEnded(context: Context, number: String?) {
        //Toast.makeText(context, "onOutgoingCallEnded", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onOutgoingCallEnded, number: $number")

        changeState(context, MyPhoneStates.IDLE)
    }

    override fun onMissedCall(context: Context, number: String?) {
        //Toast.makeText(context, "onMissedCall", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onMissedCall, number: $number")

        changeState(context, MyPhoneStates.IDLE)
    }

    override fun changeState(context: Context, state: String) {
        val statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)
        //var state = statePreferences.getString("state", MyPhoneStates.IDLE)

        val editor = statePreferences.edit()
        editor.putString("state", state)
        editor.apply()

        //Esto esta asi para que mande un mensaje de broadcast para que se actualize la vista en main despues de cambiar las preferencias
        super.changeState(context, state)
    }

}
