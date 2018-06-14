package com.gnommostudios.widgetestados.views.widgets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.gnommostudios.widgetestados.R

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
        val phone = mCollection[position]
        val view = RemoteViews(mContext!!.packageName,
                R.layout.element_list)
        view.setTextViewText(R.id.phoneTxt, mCollection[position])

        setOnClickFillInIntent(view, phone)

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

        val map = listPhonesPrefs!!.all as Map<String, *>

        for (i in map.keys)
            mCollection.add("${i.split("_")[1]} - ${map[i]!!}")


    }

    private fun setOnClickFillInIntent(views: RemoteViews, phone: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${phone.split(" - ")[1]}"))
        views.setOnClickFillInIntent(R.id.element_list, intent)
    }

}
