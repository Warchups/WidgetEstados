package com.gnommostudios.widgetestados

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.content.Intent
import android.util.Log


class ChangeStateWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, javaClass)
            intent.action = action
            Log.i("WIDGET", action)
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.change_state_widget)
            //views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setOnClickPendingIntent(R.id.button_widget_1,
                    getPendingSelfIntent(context, "Prueba1"))
            //var stateButton1 = findViewById(R.id.button_widget_1)
            //var stateButton2 = findViewById(R.id.button_widget_2)
            //var stateButton3 = findViewById(R.id.button_widget_3)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

