package com.wizsmith.lockmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.databinding.FragmentHomeBinding
import com.wizsmith.lockmanager.utils.showToast
import com.wizsmith.lockmanager.viewmodels.LockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LockViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardMyLocks.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_locks)
        }

        binding.cardAddLock.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_scan_lock)
        }

        binding.cardSyncLocks.setOnClickListener {
            viewModel.syncLocksFromServer()
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.syncLocksFromServer()
        }
    }

    private fun setupObservers() {
        viewModel.locks.observe(viewLifecycleOwner) { locks ->
            binding.tvLockCount.text = locks.size.toString()
            
            val lockedCount = locks.count { it.isLocked }
            val unlockedCount = locks.size - lockedCount
            
            binding.tvLockedCount.text = lockedCount.toString()
            binding.tvUnlockedCount.text = unlockedCount.toString()
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                requireContext().showToast(message)
            }.onFailure { error ->
                requireContext().showToast("Error: ${error.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}