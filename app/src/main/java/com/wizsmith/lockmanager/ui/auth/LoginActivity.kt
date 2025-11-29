package com.wizsmith.lockmanager.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wizsmith.lockmanager.databinding.ActivityLoginBinding
import com.wizsmith.lockmanager.ui.dashboard.DashboardActivity
import com.wizsmith.lockmanager.data.repository.AuthRepository
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val repo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val user = binding.username.text.toString().trim()
        val pass = binding.password.text.toString().trim()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = repo.login(user, pass)

                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
