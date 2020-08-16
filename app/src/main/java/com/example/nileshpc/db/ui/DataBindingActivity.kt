package com.example.nileshpc.db.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.android.databinding.basicsample.data.Popularity
import com.example.android.databinding.basicsample.data.SimpleViewModel
import com.example.nileshpc.R
import com.example.nileshpc.databinding.ActivityDataBindingBinding

class DataBindingActivity : AppCompatActivity() {
    // Obtain ViewModel from ViewModelProvider
    private val viewModel by lazy { ViewModelProvider(this).get(SimpleViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityDataBindingBinding>(
            this,
            R.layout.activity_data_binding
        )
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        //binding.name = "temp"
        //binding.lastname = "temp2"
    }

}