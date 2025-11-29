package com.wizsmith.lockmanager.ui.locks

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.databinding.FragmentLockDetailsBinding
import com.wizsmith.lockmanager.ui.adapters.EventsAdapter
import com.wizsmith.lockmanager.utils.*
import com.wizsmith.lockmanager.viewmodels.LockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockDetailsFragment : Fragment() {

    private var _binding: FragmentLockDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LockViewModel by viewModels()
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockDetailsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        eventsAdapter = EventsAdapter()
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = eventsAdapter
    }

    private fun setupClickListeners() {
        binding.btnUnlock.setOnClickListener {
            viewModel.selectedLock.value?.let { lock ->
                viewModel.unlockLock(lock)
            }
        }

        binding.btnLock.setOnClickListener {
            viewModel.selectedLock.value?.let { lock ->
                viewModel.lockLock(lock)
            }
        }

        binding.btnRefreshBattery.setOnClickListener {
            viewModel.selectedLock.value?.let { lock ->
                viewModel.getBatteryLevel(lock)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedLock.observe(viewLifecycleOwner) { lock ->
            lock?.let {
                binding.tvLockName.text = it.lockName
                binding.tvLockMac.text = "MAC: ${it.lockMac}"
                binding.tvBattery.text = it.electricQuantity.toBatteryLevel()
                binding.tvLastUpdate.text = "Last update: ${it.lastUpdateTime.toFormattedDate()}"
                
                // Update battery color
                when {
                    it.electricQuantity < Constants.BATTERY_CRITICAL_THRESHOLD -> {
                        binding.tvBattery.setTextColor(resources.getColor(R.color.error, null))
                    }
                    it.electricQuantity < Constants.BATTERY_LOW_THRESHOLD -> {
                        binding.tvBattery.setTextColor(resources.getColor(R.color.warning, null))
                    }
                    else -> {
                        binding.tvBattery.setTextColor(resources.getColor(R.color.success, null))
                    }
                }

                // Update lock status
                if (it.isLocked) {
                    binding.ivLockStatus.setImageResource(R.drawable.ic_lock)
                    binding.tvLockStatus.text = "Locked"
                    binding.btnUnlock.isEnabled = true
                    binding.btnLock.isEnabled = false
                } else {
                    binding.ivLockStatus.setImageResource(R.drawable.ic_unlock)
                    binding.tvLockStatus.text = "Unlocked"
                    binding.btnUnlock.isEnabled = false
                    binding.btnLock.isEnabled = true
                }

                // Load events
                viewModel.getEventsForLock(it.lockId).observe(viewLifecycleOwner) { events ->
                    if (events.isEmpty()) {
                        binding.tvNoEvents.visible()
                        binding.rvEvents.gone()
                    } else {
                        binding.tvNoEvents.gone()
                        binding.rvEvents.visible()
                        eventsAdapter.submitList(events)
                    }
                }
            }
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                requireContext().showToast(message)
            }.onFailure { error ->
                requireContext().showToast("Error: ${error.message}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lock_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                showDeleteConfirmation()
                true
            }
            R.id.action_rename -> {
                showRenameDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Lock")
            .setMessage("Are you sure you want to delete this lock?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.selectedLock.value?.let { lock ->
                    viewModel.deleteLock(lock)
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRenameDialog() {
        // TODO: Implement rename dialog
        requireContext().showToast("Rename feature coming soon")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}