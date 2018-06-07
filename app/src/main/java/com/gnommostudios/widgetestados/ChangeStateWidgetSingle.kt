package com.gnommostudios.widgetestados

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import android.content.ComponentName

class ChangeStateWidgetSingle : AppWidgetProvider() {

    private val BUTTON = "BUTTON"

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.change_state_widget_single)

            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = statePreferences!!.getString("state", "")

            views.setTextViewText(R.id.appwidget_text, state)

            views.setOnClickPendingIntent(R.id.appwidget_text,
                   getPendingSelfIntent(context, BUTTON))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == BUTTON) {
            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = statePreferences!!.getString("state", "")

            when (state) {
                ":)" -> state = ":("
                ":(" -> state = ":D"
                ":D" -> state = ":)"
            }

            val editor = statePreferences!!.edit()

            editor.putString("state", state)

            editor.commit()

            /*val views = RemoteViews(context.packageName, R.layout.change_state_widget_single)
            views.setTextViewText(R.id.appwidget_text, state)*/

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val thisAppWidget = ComponentName(context.packageName, ChangeStateWidgetSingle::class.java!!.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text)

            onUpdate(context, appWidgetManager, appWidgetIds)

            /*val initialUpdateIntent = Intent(
                    AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            initialUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            context.sendBroadcast(initialUpdateIntent)*/
        }

        /*if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val thisAppWidget = ComponentName(context.packageName, ChangeStateWidgetSingle::class.java!!.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text)
            Log.e("working :", "finally after a whole day")

        }*/
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }
}

