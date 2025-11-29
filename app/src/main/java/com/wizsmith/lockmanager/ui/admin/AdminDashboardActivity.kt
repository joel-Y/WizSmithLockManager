package com.wizsmith.lockmanager.ui.admin

import android.os.Bundle
import com.wizsmith.lockmanager.ui.BaseDrawerActivity
import com.wizsmith.lockmanager.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : BaseDrawerActivity() {
    private lateinit var adminBinding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminBinding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        // put the activity content into the drawer's content container
        binding.contentContainer.removeAllViews()
        binding.contentContainer.addView(adminBinding.root)
        title = "Admin Dashboard"
        // TODO: load admin summary data
    }
}
