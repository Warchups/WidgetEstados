package com.gnommostudios.widgetestados.views.widgets

import android.content.Intent
import android.widget.RemoteViewsService

class ListWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return ListDataProvider(this)
    }

}
