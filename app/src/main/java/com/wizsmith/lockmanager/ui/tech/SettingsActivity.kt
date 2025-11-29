package com.wizsmith.lockmanager.ui.tech

import android.os.Bundle
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.ui.base.BaseDrawerActivity

class SettingsActivity : BaseDrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.contentTitle.text = "Settings"
        binding.contentContainer.addView(
            layoutInflater.inflate(R.layout.activity_settings, null)
        )
    }
}
