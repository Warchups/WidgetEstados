package com.gnommostudios.widgetestados.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

//https://gist.github.com/ftvs/e61ccb039f511eb288ee <-- Lo he sacado de aqui

abstract class PhoneCallReceiver : BroadcastReceiver() {

    companion object {
        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var isIncoming: Boolean = false
        private var savedNumber: String? = null  //because the passed incoming is only valid in ringing
    }

    override fun onReceive(context: Context, intent: Intent) {
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            savedNumber = intent.extras!!.getString("android.intent.extra.PHONE_NUMBER")
        } else {
            val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
            val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            var state = 0
            when (stateStr) {
                TelephonyManager.EXTRA_STATE_IDLE -> state = TelephonyManager.CALL_STATE_IDLE
                TelephonyManager.EXTRA_STATE_OFFHOOK -> state = TelephonyManager.CALL_STATE_OFFHOOK
                TelephonyManager.EXTRA_STATE_RINGING -> state = TelephonyManager.CALL_STATE_RINGING
            }

            onCallStateChanged(context, state, number)
        }
    }

    protected open fun onIncomingCallStarted(context: Context, number: String?) {}
    protected open fun onOutgoingCallStarted(context: Context, number: String?) {}
    protected open fun onIncomingCallEnded(context: Context, number: String?) {}
    protected open fun onOutgoingCallEnded(context: Context, number: String?) {}
    protected open fun onMissedCall(context: Context, number: String?) {}

    protected open fun changeState(context: Context, state: String) {
        val i = Intent("MainActivity")
        i.putExtra("UPDATE", true)
        context.applicationContext.sendBroadcast(i)
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private fun onCallStateChanged(context: Context, state: Int, number: String?) {
        if (lastState == state) {
            //No change, debounce extras
            return
        }

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                savedNumber = number
                onIncomingCallStarted(context, number)
            }

            TelephonyManager.CALL_STATE_OFFHOOK -> {
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    onOutgoingCallStarted(context, savedNumber)
                }
            }

            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                when {
                    lastState == TelephonyManager.CALL_STATE_RINGING -> onMissedCall(context, savedNumber)

                    isIncoming -> onIncomingCallEnded(context, savedNumber)

                    else -> onOutgoingCallEnded(context, savedNumber)
                }
        }

        lastState = state
    }
}