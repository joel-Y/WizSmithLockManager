package com.wizsmith.lockmanager.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wizsmith.lockmanager.databinding.ActivityBaseDrawerBinding

open class BaseDrawerActivity : AppCompatActivity() {

    protected lateinit var binding: ActivityBaseDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
