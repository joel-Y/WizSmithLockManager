package com.wizsmith.lockmanager.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.data.models.Lock
import com.wizsmith.lockmanager.databinding.ItemLockBinding
import com.wizsmith.lockmanager.utils.toBatteryLevel
import com.wizsmith.lockmanager.utils.toFormattedDate

class LocksAdapter(
    private val onLockClick: (Lock) -> Unit,
    private val onUnlockClick: (Lock) -> Unit,
    private val onLockClick: (Lock) -> Unit
) : ListAdapter<Lock, LocksAdapter.LockViewHolder>(LockDiffCallback()) {

    private var originalList: List<Lock> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockViewHolder {
        val binding = ItemLockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Lock>?) {
        originalList = list ?: emptyList()
        super.submitList(list)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { lock ->
                lock.lockName.contains(query, ignoreCase = true) ||
                lock.lockMac.contains(query, ignoreCase = true)
            }
        }
        super.submitList(filteredList)
    }

    inner class LockViewHolder(
        private val binding: ItemLockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lock: Lock) {
            binding.apply {
                tvLockName.text = lock.lockName
                tvLockMac.text = lock.lockMac
                tvBattery.text = lock.electricQuantity.toBatteryLevel()
                tvLastUpdate.text = lock.lastUpdateTime.toFormattedDate()

                // Set lock status icon
                if (lock.isLocked) {
                    ivLockStatus.setImageResource(R.drawable.ic_lock)
                    tvLockStatus.text = "Locked"
                } else {
                    ivLockStatus.setImageResource(R.drawable.ic_unlock)
                    tvLockStatus.text = "Unlocked"
                }

                // Battery color
                when {
                    lock.electricQuantity < 10 -> {
                        tvBattery.setTextColor(root.context.getColor(R.color.error))
                    }
                    lock.electricQuantity < 20 -> {
                        tvBattery.setTextColor(root.context.getColor(R.color.warning))
                    }
                    else -> {
                        tvBattery.setTextColor(root.context.getColor(R.color.success))
                    }
                }

                root.setOnClickListener { onLockClick(lock) }

                btnQuickAction.setOnClickListener {
                    if (lock.isLocked) {
                        onUnlockClick(lock)
                    } else {
                        onLockClick(lock)
                    }
                }

                btnQuickAction.text = if (lock.isLocked) "Unlock" else "Lock"
            }
        }
    }

    private class LockDiffCallback : DiffUtil.ItemCallback<Lock>() {
        override fun areItemsTheSame(oldItem: Lock, newItem: Lock): Boolean {
            return oldItem.lockId == newItem.lockId
        }

        override fun areContentsTheSame(oldItem: Lock, newItem: Lock): Boolean {
            return oldItem == newItem
        }
    }
}