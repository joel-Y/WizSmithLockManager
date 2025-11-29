package com.wizsmith.lockmanager.ui.tech

import android.os.Bundle
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.ui.base.BaseDrawerActivity

class LockSetupActivity : BaseDrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.contentTitle.text = "Lock Setup"
        binding.contentContainer.addView(
            layoutInflater.inflate(R.layout.activity_lock_setup, null)
        )
    }
}
