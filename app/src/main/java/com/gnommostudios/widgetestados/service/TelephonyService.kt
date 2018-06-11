package com.gnommostudios.widgetestados.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class TelephonyService : IntentService("TelephonyService") {

    private var mTelephonyManager: TelephonyManager? = null

    override fun onCreate() {
        super.onCreate()
        mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        mTelephonyManager!!.listen(PhoneCallback(this), PhoneStateListener.LISTEN_CALL_STATE
                or PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
                or PhoneStateListener.LISTEN_CELL_LOCATION
                or PhoneStateListener.LISTEN_DATA_ACTIVITY
                or PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                or PhoneStateListener.LISTEN_SERVICE_STATE
                or PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                or PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                or PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR)
    }

    override fun onHandleIntent(p0: Intent?) {

    }

}
