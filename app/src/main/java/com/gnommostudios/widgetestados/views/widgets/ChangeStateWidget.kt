package com.gnommostudios.widgetestados.views.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*
import android.widget.RemoteViews
import com.gnommostudios.widgetestados.R
import com.gnommostudios.widgetestados.utils.MyPhoneStates

class ChangeStateWidget : AppWidgetProvider() {

    companion object {
        private const val BUTTON_1 = MyPhoneStates.TALKING
        private const val BUTTON_2 = MyPhoneStates.IDLE
        private const val BUTTON_3 = MyPhoneStates.BLOCK
    }

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.change_state_widget)

            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = statePreferences!!.getString("state", MyPhoneStates.IDLE)

            when (state) {
                MyPhoneStates.TALKING -> {
                    views.setImageViewResource(R.id.buttonWidget1, R.drawable.baseline_call_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget2, R.drawable.outline_call_end_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget3, R.drawable.outline_cancel_black_18dp)
                }
                MyPhoneStates.IDLE -> {
                    views.setImageViewResource(R.id.buttonWidget1, R.drawable.outline_call_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget2, R.drawable.baseline_call_end_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget3, R.drawable.outline_cancel_black_18dp)
                }
                MyPhoneStates.BLOCK -> {
                    views.setImageViewResource(R.id.buttonWidget1, R.drawable.outline_call_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget2, R.drawable.outline_call_end_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget3, R.drawable.baseline_cancel_black_18dp)
                }
                else -> {
                    views.setImageViewResource(R.id.buttonWidget1, R.drawable.outline_call_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget2, R.drawable.outline_call_end_black_18dp)
                    views.setImageViewResource(R.id.buttonWidget3, R.drawable.outline_cancel_black_18dp)
                }
            }

            views.setOnClickPendingIntent(R.id.buttonWidget1,
                    getPendingSelfIntent(context, BUTTON_1))
            views.setOnClickPendingIntent(R.id.buttonWidget2,
                    getPendingSelfIntent(context, BUTTON_2))
            views.setOnClickPendingIntent(R.id.buttonWidget3,
                    getPendingSelfIntent(context, BUTTON_3))

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
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

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)

            val thisAppWidget = ComponentName(context.packageName, ChangeStateWidget::class.java.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text)

            onUpdate(context, appWidgetManager, appWidgetIds)
        }

        if (intent.action == BUTTON_1 || intent.action == BUTTON_2 || intent.action == BUTTON_3) {

            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = intent.action

            val editor = statePreferences!!.edit()

            editor.putString("state", state)

            editor.apply()

            val i = Intent("MainActivity")
            i.putExtra("UPDATE", true)
            context.applicationContext.sendBroadcast(i)

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val thisAppWidget = ComponentName(context.packageName, ChangeStateWidget::class.java.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text)

            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

}
