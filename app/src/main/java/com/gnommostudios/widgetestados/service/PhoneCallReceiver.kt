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

    //Funciones para sobreescribir
    protected open fun onIncomingCallStarted(context: Context, number: String?) {}
    protected open fun onOutgoingCallStarted(context: Context, number: String?) {}
    protected open fun onIncomingCallEnded(context: Context, number: String?) {}
    protected open fun onOutgoingCallEnded(context: Context, number: String?) {}
    protected open fun onMissedCall(context: Context, number: String?) {}
    protected open fun changeState(context: Context, state: String) {
        //Mensaje de broadcas para que main actualize
        val i = Intent("MainActivity")
        i.putExtra("UPDATE", true)
        context.applicationContext.sendBroadcast(i)
    }

    override fun onReceive(context: Context, intent: Intent) {
        //El action NEW_OUTGOING_CALL da lugar cuando llama el mismo movil
        if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            //Si el action es el anterior, tiene un extra que es el PHONE_NUMBER
            savedNumber = intent.extras!!.getString("android.intent.extra.PHONE_NUMBER")
        } else {
            //Recoge el estado del telefono
            val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
            //Recoge el numero de entrada (Si existe)
            val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            var state = 0
            //Mira si el estado es IDLE, OFFHOOK o RINGING, y lo guarda en la variable state
            when (stateStr) {
                TelephonyManager.EXTRA_STATE_IDLE -> state = TelephonyManager.CALL_STATE_IDLE
                TelephonyManager.EXTRA_STATE_OFFHOOK -> state = TelephonyManager.CALL_STATE_OFFHOOK
                TelephonyManager.EXTRA_STATE_RINGING -> state = TelephonyManager.CALL_STATE_RINGING
            }

            //Llama a la funcion que comprueba el estado
            onCallStateChanged(context, state, number)
        }
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private fun onCallStateChanged(context: Context, state: Int, number: String?) {
        //Si el ultimo estado (de primeras IDLE), es igual al estado actual, se sale
        if (lastState == state) {
            return
        }

        when (state) {
            //Si el estado es RINGING (te estan llamando)
            TelephonyManager.CALL_STATE_RINGING -> {
                //Marca la variable isIncoming como true
                isIncoming = true
                //Guarda el numero en la variable savedNumber
                savedNumber = number
                //Llamo al metodo onIncomingCallStarted
                onIncomingCallStarted(context, savedNumber)
            }

            //Si el estado es OFFHOOK (Se ha descolgado el telefono)
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them//

                //Si el estado anterior no era el RINGING significa que estabas llamando tu
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    //Marca la variable isIncoming como false
                    isIncoming = false
                    //Llamo al metodo onOutgoingCallStarted
                    onOutgoingCallStarted(context, savedNumber)
                }
            }

            //Si el estado es IDLE (No hay ninguna llamada activa o no te estan llamando)
            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)//
                when {
                    //Si el estado anterior era RINGING, significa que se a colgado sin contestar
                    lastState == TelephonyManager.CALL_STATE_RINGING -> onMissedCall(context, savedNumber)

                    //Si no se cumple la condicion anterior y isIncoming es true, significa que era una llamada entrante y se ha colgado
                    isIncoming -> onIncomingCallEnded(context, savedNumber)

                    //Si no es ninguna de las anteriores, significa que era una llamada saliente y se ha colgado
                    else -> onOutgoingCallEnded(context, savedNumber)
                }
        }

        lastState = state
    }
}