package com.gnommostudios.widgetestados.utils

import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.widget.RemoteViews

object RemoteViewsUtil {

    private const val METHOD_SET_BACKGROUND = "setBackgroundResource"

    fun setBackground(views: RemoteViews, @IdRes view: Int, @DrawableRes background: Int) {
        views.setInt(view, METHOD_SET_BACKGROUND, background)
    }

}
