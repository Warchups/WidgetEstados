package com.gnommostudios.widgetestados

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast


class ChangeStateWidget : AppWidgetProvider() {

    private val BUTTON_1 = "call"
    private val BUTTON_2 = "end"
    private val BUTTON_3 = "cancel"

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val thisWidget = ComponentName(context, ChangeStateWidget::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.change_state_widget)

            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = statePreferences!!.getString("state", "")

            when (state) {
                "call" -> {
                    views.setImageViewResource(R.id.button_widget_1, R.drawable.baseline_call_black_18dp)
                    views.setImageViewResource(R.id.button_widget_2, R.drawable.outline_call_end_black_18dp)
                    views.setImageViewResource(R.id.button_widget_3, R.drawable.outline_cancel_black_18dp)
                }
                "end" -> {
                    views.setImageViewResource(R.id.button_widget_1, R.drawable.outline_call_black_18dp)
                    views.setImageViewResource(R.id.button_widget_2, R.drawable.baseline_call_end_black_18dp)
                    views.setImageViewResource(R.id.button_widget_3, R.drawable.outline_cancel_black_18dp)
                }
                "cancel" -> {
                    views.setImageViewResource(R.id.button_widget_1, R.drawable.outline_call_black_18dp)
                    views.setImageViewResource(R.id.button_widget_2, R.drawable.outline_call_end_black_18dp)
                    views.setImageViewResource(R.id.button_widget_3, R.drawable.baseline_cancel_black_18dp)
                }
                else -> {
                    views.setImageViewResource(R.id.button_widget_1, R.drawable.outline_call_black_18dp)
                    views.setImageViewResource(R.id.button_widget_2, R.drawable.outline_call_end_black_18dp)
                    views.setImageViewResource(R.id.button_widget_3, R.drawable.outline_cancel_black_18dp)
                }
            }

            views.setOnClickPendingIntent(R.id.button_widget_1,
                    getPendingSelfIntent(context, BUTTON_1))
            views.setOnClickPendingIntent(R.id.button_widget_2,
                    getPendingSelfIntent(context, BUTTON_2))
            views.setOnClickPendingIntent(R.id.button_widget_3,
                    getPendingSelfIntent(context, BUTTON_3))

            //updateAppWidget(context, appWidgetManager, appWidgetId)
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        Log.i("WIDGET", action)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == BUTTON_1 || intent.action == BUTTON_2 || intent.action == BUTTON_3) {

            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = intent.action

            val editor = statePreferences!!.edit()

            editor.putString("state", state)

            editor.commit()

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val thisAppWidget = ComponentName(context.packageName, ChangeStateWidget::class.java!!.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text)

            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

}

