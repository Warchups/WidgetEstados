package com.gnommostudios.widgetestados.views.widgets

import android.content.*
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import java.util.ArrayList



class WidgetDataProvider(context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private var mCollection: MutableList<String> = ArrayList()
    private var mContext: Context? = null
    private var listPhonesPrefs: SharedPreferences? = null

    init {
        mContext = context
        listPhonesPrefs = mContext!!.getSharedPreferences("phones", Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        initData()
    }

    override fun onDataSetChanged() {
        initData()
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return mCollection.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val view = RemoteViews(mContext!!.packageName,
                android.R.layout.simple_list_item_1)
        view.setTextViewText(android.R.id.text1, mCollection[position])
        return view
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun initData() {
        mCollection.clear()

        val map = listPhonesPrefs!!.all as Map<String, String>

        for (i in map.keys)
            mCollection.add("${i.split("_")[1]} - ${map[i]!!}")


    }

    companion object {

        private val TAG = "WidgetDataProvider"
    }

}
