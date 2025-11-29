package com.wizsmith.lockmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.databinding.ActivityLoginBinding
import com.wizsmith.lockmanager.ui.MainActivity
import com.wizsmith.lockmanager.utils.gone
import com.wizsmith.lockmanager.utils.showToast
import com.wizsmith.lockmanager.utils.visible
import com.wizsmith.lockmanager.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                viewModel.login(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            return false
        }
        binding.tilUsername.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        binding.tilPassword.error = null

        return true
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnLogin.isEnabled = false
            } else {
                binding.progressBar.gone()
                binding.btnLogin.isEnabled = true
            }
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                showToast("Login successful!")
                navigateToMain()
            }.onFailure { error ->
                showToast("Login failed: ${error.message}")
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
