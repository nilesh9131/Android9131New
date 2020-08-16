package com.example.nileshpc.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.example.nileshpc.R
import com.example.nileshpc.widget.view.MyWidgetRemoteViewsService
import java.text.DateFormat
import java.util.*

const val WIDGET_IDS_KEY = "mywidgetproviderwidgetids"

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    var intentValue: Int? = 0
    var dateString: String = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
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


    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences(
            mSharedPrefFile, 0
        )
        var count = prefs.getInt(COUNT_KEY + appWidgetId, 0)
        count++
        //val widgetText = context.getString(R.string.appwidget_text)
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        views.setTextViewText(R.id.appwidget_id, appWidgetId.toString())
        views.setTextViewText(
            R.id.appwidget_update,
            context.getResources().getString(
                R.string.date_count_format, count, dateString
            )
        );
        val prefEditor = prefs.edit()
        prefEditor.putInt(COUNT_KEY + appWidgetId, count)
        prefEditor.apply()

        val intentUpdate = Intent(context, NewAppWidget::class.java)
        intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val idArray = intArrayOf(appWidgetId)
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        val pendingUpdate = PendingIntent.getBroadcast(
            context, appWidgetId, intentUpdate,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.button_update, pendingUpdate)


        val intent = Intent(context, MyWidgetRemoteViewsService::class.java)
        views.setRemoteAdapter(R.id.widgetListView, intent)

        Toast.makeText(context, ""+intentValue, Toast.LENGTH_LONG).show()
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.hasExtra(WIDGET_IDS_KEY) != null) {
            intentValue = intent.getIntExtra(WIDGET_IDS_KEY, 0)
        }
        super.onReceive(context, intent)
    }


    companion object {
        private const val mSharedPrefFile = "com.example.android.appwidgetsample"
        private const val COUNT_KEY = "count"
    }
}