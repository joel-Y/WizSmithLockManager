package com.wizsmith.lockmanager.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wizsmith.lockmanager.databinding.FragmentSettingsBinding
import com.wizsmith.lockmanager.ui.auth.LoginActivity
import com.wizsmith.lockmanager.utils.PreferenceManager
import com.wizsmith.lockmanager.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadUserInfo()
        setupClickListeners()
    }

    private fun loadUserInfo() {
        binding.tvUsername.text = preferenceManager.getUsername() ?: "User"
        binding.tvUserId.text = "ID: ${preferenceManager.getUserId() ?: "N/A"}"
    }

    private fun setupClickListeners() {
        binding.cardLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        binding.cardAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                viewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("About WizSmith")
            .setMessage("WizSmith Lock Manager v1.0.0\n\nA professional smart lock management solution powered by TTLock SDK.\n\n© 2024 WizSmith Inc.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}