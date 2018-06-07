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

    private val BUTTON_1 = ":)"
    private val BUTTON_2 = ":("
    private val BUTTON_3 = ":D"

    private var statePreferences: SharedPreferences? = null

    private var state: String? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val thisWidget = ComponentName(context, ChangeStateWidget::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.change_state_widget)

            statePreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)

            state = statePreferences!!.getString("state", "")

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

            //var button1 = ((context as Activity).findViewById(R.id.button_widget_1) as Button)

            //var prueba1 = (context as Activity)

            val editor = statePreferences!!.edit()

            editor.putString("state", state)

            editor.commit()

            changeButtons(context)
        }
    }

    private fun changeButtons(context: Context) {
        state = statePreferences!!.getString("state", "")

        when (state) {
            ":)" -> {
                Toast.makeText(context, "Hola", Toast.LENGTH_SHORT).show()
                /*widgetButton1!!.isEnabled = false
                widgetButton2!!.isEnabled = true
                widgetButton3!!.isEnabled = true

                widgetButton1!!.setBackgroundColor(Color.GREEN)
                widgetButton2!!.setBackgroundColor(Color.parseColor("#8555"))
                widgetButton3!!.setBackgroundColor(Color.parseColor("#8555"))*/
            }
            ":(" -> {
                Toast.makeText(context, "Adios", Toast.LENGTH_SHORT).show()
                /*widgetButton1!!.isEnabled = true
                widgetButton2!!.isEnabled = false
                widgetButton3!!.isEnabled = true

                widgetButton1!!.setBackgroundColor(Color.parseColor("#8555"))
                widgetButton2!!.setBackgroundColor(Color.parseColor("#8111"))
                widgetButton3!!.setBackgroundColor(Color.parseColor("#8555"))*/
            }
            ":D" -> {
                Toast.makeText(context, "Que tal?", Toast.LENGTH_SHORT).show()
                /*widgetButton1!!.isEnabled = true
                widgetButton2!!.isEnabled = true
                widgetButton3!!.isEnabled = false

                widgetButton1!!.setBackgroundColor(Color.parseColor("#8555"))
                widgetButton2!!.setBackgroundColor(Color.parseColor("#8555"))
                widgetButton3!!.setBackgroundColor(Color.parseColor("#8111"))*/
            }
            else -> {
                /*widgetButton1!!.isEnabled = true
                widgetButton2!!.isEnabled = true
                widgetButton3!!.isEnabled = true

                widgetButton1!!.setBackgroundColor(Color.parseColor("#8555"))
                widgetButton2!!.setBackgroundColor(Color.parseColor("#8555"))
                widgetButton3!!.setBackgroundColor(Color.parseColor("#8555"))*/
            }
        }
    }

}

