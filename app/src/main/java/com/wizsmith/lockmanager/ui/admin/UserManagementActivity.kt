package com.wizsmith.lockmanager.ui.admin

import android.os.Bundle
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.ui.base.BaseDrawerActivity

class UserManagementActivity : BaseDrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.contentTitle.text = "User Management"
        binding.contentContainer.addView(
            layoutInflater.inflate(R.layout.activity_settings, null)
        )
    }
}
