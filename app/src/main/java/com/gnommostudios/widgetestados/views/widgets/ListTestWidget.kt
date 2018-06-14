package com.gnommostudios.widgetestados.views.widgets

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

import com.gnommostudios.widgetestados.R

class ListTestWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent!!.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)

            val thisAppWidget = ComponentName(context!!.packageName, ListTestWidget::class.java.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.list_test_widget)
        //        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views)
        } else {
            setRemoteAdapterV11(context, views)
        }

        views.setPendingIntentTemplate(R.id.widget_list, getStringPendingIntent(context))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getStringPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(context, 0, Intent(Intent.ACTION_VIEW), 0)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun setRemoteAdapter(context: Context, views: RemoteViews) {
        views.setRemoteAdapter(R.id.widget_list,
                Intent(context, WidgetService::class.java))
    }

    @Suppress("DEPRECATION")
    private fun setRemoteAdapterV11(context: Context, views: RemoteViews) {
        views.setRemoteAdapter(0, R.id.widget_list,
                Intent(context, WidgetService::class.java))
    }

}

