package com.example.nileshpc.widget.view

import android.content.Intent
import android.widget.RemoteViewsService


class MyWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory? {
        return MyWidgetRemoteViewsFactory(this.applicationContext, intent)
    }
}