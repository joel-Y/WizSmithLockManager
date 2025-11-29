package com.wizsmith.lockmanager.ui.locks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsmith.lockmanager.databinding.ItemLockBinding
import com.wizsmith.lockmanager.model.Lock

class LocksAdapter : RecyclerView.Adapter<LocksAdapter.VH>() {
    private val items = ArrayList<Lock>()

    fun submitList(list: List<Lock>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemLockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lock: Lock) {
            binding.tvName.text = lock.lockName ?: "Unknown"
            binding.tvStatus.text = lock.status ?: "N/A"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemLockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(vb)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size
}
