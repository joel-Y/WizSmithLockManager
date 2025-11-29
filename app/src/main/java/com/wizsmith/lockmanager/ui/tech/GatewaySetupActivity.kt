package com.wizsmith.lockmanager.ui.tech

import android.os.Bundle
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.ui.base.BaseDrawerActivity

class GatewaySetupActivity : BaseDrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.contentTitle.text = "Gateway Setup"
        binding.contentContainer.addView(
            layoutInflater.inflate(R.layout.activity_gateway_setup, null)
        )
    }
}
