package com.wizsmith.lockmanager.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wizsmith.lockmanager.api.RetrofitClient
import com.wizsmith.lockmanager.databinding.ActivityLoginBinding
import com.wizsmith.lockmanager.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            doLogin(username, password)
        }
    }

    private fun doLogin(username: String, password: String) {
        binding.btnLogin.isEnabled = false
        lifecycleScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.login(
                        SessionManager.clientId,
                        SessionManager.clientSecret,
                        username,
                        password
                    )
                }
                if (!resp.accessToken.isNullOrBlank()) {
                    SessionManager.accessToken = resp.accessToken
                    // Get user detail to figure role. If your API returns role in login, use that.
                    val detail = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.getUserDetail()
                    }
                    val role = detail.role ?: "technician"
                    SessionManager.userRole = role
                    // Navigate based on role
                    if (role.equals("admin", true)) {
                        startActivity(Intent(this@LoginActivity, com.wizsmith.lockmanager.ui.admin.AdminDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, com.wizsmith.lockmanager.ui.tech.TechnicianDashboardActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: empty token", Toast.LENGTH_LONG).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(this@LoginActivity, "Login error: ${ex.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }
    }
}
