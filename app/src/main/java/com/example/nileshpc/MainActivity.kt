package com.example.nileshpc

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.android.hilt.ui.MainActivity
import com.example.nileshpc.camera.CameraXActivity
import com.example.nileshpc.db.ui.DataBindingActivity
import com.example.nileshpc.motionlayout.AnimationBasicActivity
import com.example.nileshpc.widget.AppWidgetActivity
import com.example.nileshpc.widget.NewAppWidget
import com.example.nileshpc.widget.WIDGET_IDS_KEY
import com.example.nileshpc.widget.pinning.WidgetPinningActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleClickListener()
    }

    /**
     * This function will handle all the click events for the view
     */
    private fun handleClickListener() {
        cameraButton.setOnClickListener {
            val cameraIntent = Intent(this, CameraXActivity::class.java)
            startActivity(cameraIntent)
        }

        hiltButton.setOnClickListener {
            val hiltIntent = Intent(this, MainActivity::class.java)
            startActivity(hiltIntent)
        }

        animationButton.setOnClickListener {
            val animationIntent = Intent(this, AnimationBasicActivity::class.java)
            startActivity(animationIntent)
            //https://developer.android.com/topic/libraries/data-binding/expressions#expression_language
        }

        widgetButton.setOnClickListener {
         //   updateMyWidgets(this@MainActivity, null)
            //val animationIntent = Intent(this, AppWidgetActivity::class.java)
            val animationIntent = Intent(this, WidgetPinningActivity::class.java)
            startActivity(animationIntent)
        }

        dataBindingButton.setOnClickListener {
            val dataBindingIntent = Intent(this, DataBindingActivity::class.java)
            startActivity(dataBindingIntent)
        }

    }

    fun updateMyWidgets(context: Context, data: Parcelable?) {
        val man = AppWidgetManager.getInstance(context)
        val ids = man.getAppWidgetIds(
            ComponentName(context, NewAppWidget::class.java)
        )
        val updateIntent = Intent()
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        updateIntent.putExtra(WIDGET_IDS_KEY, 3)
        context.sendBroadcast(updateIntent)
    }
}
/*
Automate test case
https://developer.android.com/studio/test/espresso-test-recorder
 */
/**
 * Android slicing
 * 
 * https://codelabs.developers.google.com/codelabs/android-slices-basic/index.html?index=..%2F..index#0
 */

