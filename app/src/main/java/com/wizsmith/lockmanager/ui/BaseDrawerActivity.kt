package com.wizsmith.lockmanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.databinding.ActivityDrawerBinding
import com.wizsmith.lockmanager.util.SessionManager

open class BaseDrawerActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDrawer()
    }

    private fun setupDrawer() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // default - stay
                }
                R.id.nav_locks -> startActivity(Intent(this, com.wizsmith.lockmanager.ui.locks.LocksActivity::class.java))
                R.id.nav_lock_setup -> startActivity(Intent(this, com.wizsmith.lockmanager.ui.tech.LockSetupActivity::class.java))
                R.id.nav_gateway_setup -> startActivity(Intent(this, com.wizsmith.lockmanager.ui.tech.GatewaySetupActivity::class.java))
                R.id.nav_service_tickets -> startActivity(Intent(this, com.wizsmith.lockmanager.ui.tech.ServiceTicketsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, com.wizsmith.lockmanager.ui.settings.SettingsActivity::class.java))
                R.id.nav_logout -> {
                    SessionManager.clear()
                    startActivity(Intent(this, com.wizsmith.lockmanager.ui.login.LoginActivity::class.java))
                    finishAffinity()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // Hide admin-only entries when role is technician
        val role = SessionManager.userRole
        if (role == null || !role.equals("admin", true)) {
            binding.navView.menu.findItem(R.id.nav_users)?.isVisible = false
            binding.navView.menu.findItem(R.id.nav_passcodes)?.isVisible = false
        }
    }
}
