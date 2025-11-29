package com.wizsmith.lockmanager.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice
import com.wizsmith.lockmanager.databinding.ItemScannedDeviceBinding

class ScannedDevicesAdapter(
    private val onDeviceClick: (ExtendedBluetoothDevice) -> Unit
) : ListAdapter<ExtendedBluetoothDevice, ScannedDevicesAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemScannedDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeviceViewHolder(
        private val binding: ItemScannedDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: ExtendedBluetoothDevice) {
            binding.apply {
                tvDeviceName.text = device.name ?: "Unknown Device"
                tvDeviceMac.text = device.address
                tvRssi.text = "Signal: ${device.rssi} dBm"
                
                device.battery?.let {
                    tvBattery.text = "$it%"
                }

                root.setOnClickListener { onDeviceClick(device) }
            }
        }
    }

    private class DeviceDiffCallback : DiffUtil.ItemCallback<ExtendedBluetoothDevice>() {
        override fun areItemsTheSame(
            oldItem: ExtendedBluetoothDevice,
            newItem: ExtendedBluetoothDevice
        ): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(
            oldItem: ExtendedBluetoothDevice,
            newItem: ExtendedBluetoothDevice
        ): Boolean {
            return oldItem.address == newItem.address &&
                    oldItem.rssi == newItem.rssi &&
                    oldItem.battery == newItem.battery
        }
    }
}
