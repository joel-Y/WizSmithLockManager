package com.wizsmith.lockmanager.ui.dashboard

import android.os.Bundle
import com.wizsmith.lockmanager.ui.base.BaseDrawerActivity

class DashboardActivity : BaseDrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.contentTitle.text = "Technician Dashboard"
        binding.contentContainer.addView(
            layoutInflater.inflate(
                com.wizsmith.lockmanager.R.layout.activity_dashboard, null
            )
        )
    }
}
