package com.gnommostudios.widgetestados.views.widgets

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return WidgetDataProvider(this)
    }
}
