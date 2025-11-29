package com.wizsmith.lockmanager.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.data.models.LockEvent
import com.wizsmith.lockmanager.databinding.ItemEventBinding
import com.wizsmith.lockmanager.utils.toFormattedDate

class EventsAdapter : ListAdapter<LockEvent, EventsAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: LockEvent) {
            binding.apply {
                tvEventType.text = event.eventType.replaceFirstChar { it.uppercase() }
                tvEventDetail.text = event.eventDetail
                tvEventTime.text = event.timestamp.toFormattedDate()
                
                event.username?.let {
                    tvUsername.text = "By: $it"
                }

                // Set icon based on event type
                when (event.eventType.lowercase()) {
                    "unlock" -> ivEventIcon.setImageResource(R.drawable.ic_unlock)
                    "lock" -> ivEventIcon.setImageResource(R.drawable.ic_lock)
                    "battery_low" -> ivEventIcon.setImageResource(R.drawable.ic_battery_low)
                    else -> ivEventIcon.setImageResource(R.drawable.ic_info)
                }
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<LockEvent>() {
        override fun areItemsTheSame(oldItem: LockEvent, newItem: LockEvent): Boolean {
            return oldItem.eventId == newItem.eventId
        }

        override fun areContentsTheSame(oldItem: LockEvent, newItem: LockEvent): Boolean {
            return oldItem == newItem
        }
    }
}