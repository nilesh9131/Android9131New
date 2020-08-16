package com.example.nileshpc.widget.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.nileshpc.R


class MyWidgetRemoteViewsFactory constructor(var mContext: Context?, intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {

    private var listData: ArrayList<String>? = null
    override fun onCreate() {
        listData = arrayListOf("AS", "DF", "GH", "HJ", "TY", "GH")

    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(mContext?.packageName, 0)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        Log.d("DataChanged", "update received")
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext!!.packageName, R.layout.collection_widget_list_item)
        rv.setTextViewText(R.id.widgetItemTaskNameLabel, listData?.get(position))

        return rv
    }

    override fun getCount(): Int {
        return listData?.size ?: 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
    }
}