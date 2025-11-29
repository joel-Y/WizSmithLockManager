package com.wizsmith.lockmanager.ui.locks

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.databinding.ActivityScanLockBinding
import com.wizsmith.lockmanager.ui.adapters.ScannedDevicesAdapter
import com.wizsmith.lockmanager.utils.*
import com.wizsmith.lockmanager.viewmodels.LockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanLockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanLockBinding
    private val viewModel: LockViewModel by viewModels()
    private lateinit var adapter: ScannedDevicesAdapter

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startScanning()
        } else {
            showToast("Bluetooth is required to scan for locks")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        checkPermissionsAndScan()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Scan for Locks"
    }

    private fun setupRecyclerView() {
        adapter = ScannedDevicesAdapter { device ->
            // Show dialog to name the lock
            showAddLockDialog(device)
        }
        binding.rvDevices.layoutManager = LinearLayoutManager(this)
        binding.rvDevices.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnScan.setOnClickListener {
            checkPermissionsAndScan()
        }
    }

    private fun setupObservers() {
        viewModel.scannedDevices.observe(this) { devices ->
            if (devices.isEmpty()) {
                binding.tvEmptyState.visible()
                binding.rvDevices.gone()
            } else {
                binding.tvEmptyState.gone()
                binding.rvDevices.visible()
                adapter.submitList(devices)
            }
        }

        viewModel.isScanning.observe(this) { isScanning ->
            if (isScanning) {
                binding.progressBar.visible()
                binding.btnScan.text = "Scanning..."
                binding.btnScan.isEnabled = false
            } else {
                binding.progressBar.gone()
                binding.btnScan.text = "Scan Again"
                binding.btnScan.isEnabled = true
            }
        }

        viewModel.operationResult.observe(this) { result ->
            result.onSuccess { message ->
                showToast(message)
            }.onFailure { error ->
                showToast("Error: ${error.message}")
            }
        }
    }

    private fun checkPermissionsAndScan() {
        if (!PermissionHelper.hasBluetoothPermissions(this)) {
            PermissionHelper.requestBluetoothPermissions(
                this,
                Constants.REQUEST_BLUETOOTH
            )
            return
        }

        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not supported on this device")
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
            return
        }

        startScanning()
    }

    private fun startScanning() {
        viewModel.scanForLocks()
    }

    private fun showAddLockDialog(device: com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice) {
        val input = android.widget.EditText(this)
        input.hint = "Enter lock name"
        input.setText("Lock ${System.currentTimeMillis() % 10000}")

        AlertDialog.Builder(this)
            .setTitle("Add Lock")
            .setMessage("Give this lock a name")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val lockName = input.text.toString().trim()
                if (lockName.isNotEmpty()) {
                    viewModel.initializeLock(device, lockName)
                    showToast("Adding lock...")
                    // Navigate back after a delay
                    binding.root.postDelayed({
                        finish()
                    }, 2000)
                } else {
                    showToast("Please enter a lock name")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == Constants.REQUEST_BLUETOOTH) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                checkPermissionsAndScan()
            } else {
                showToast("Permissions are required to scan for locks")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
