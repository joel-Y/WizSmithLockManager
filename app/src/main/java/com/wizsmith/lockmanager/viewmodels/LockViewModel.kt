package com.wizsmith.lockmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice
import com.wizsmith.lockmanager.data.models.Lock
import com.wizsmith.lockmanager.data.models.LockEvent
import com.wizsmith.lockmanager.data.repository.LockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val lockRepository: LockRepository
) : ViewModel() {

    val locks: LiveData<List<Lock>> = lockRepository.getAllLocks().asLiveData()

    private val _selectedLock = MutableLiveData<Lock?>()
    val selectedLock: LiveData<Lock?> = _selectedLock

    private val _scannedDevices = MutableLiveData<List<ExtendedBluetoothDevice>>()
    val scannedDevices: LiveData<List<ExtendedBluetoothDevice>> = _scannedDevices

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    private val _operationResult = MutableLiveData<Result<String>>()
    val operationResult: LiveData<Result<String>> = _operationResult

    fun selectLock(lock: Lock) {
        _selectedLock.value = lock
    }

    fun scanForLocks() {
        _isScanning.value = true
        viewModelScope.launch {
            val result = lockRepository.scanForLocks()
            result.onSuccess { devices ->
                _scannedDevices.value = devices
                _operationResult.value = Result.success("Found ${devices.size} locks")
            }.onFailure { error ->
                _operationResult.value = Result.failure(error)
            }
            _isScanning.value = false
        }
    }

    fun initializeLock(device: ExtendedBluetoothDevice, lockName: String) {
        viewModelScope.launch {
            val result = lockRepository.initializeLock(device, lockName)
            result.onSuccess { lock ->
                lockRepository.insertLock(lock)
                _operationResult.value = Result.success("Lock initialized successfully")
            }.onFailure { error ->
                _operationResult.value = Result.failure(error)
            }
        }
    }

    fun unlockLock(lock: Lock) {
        viewModelScope.launch {
            val result = lockRepository.unlockLock(lock)
            result.onSuccess {
                _operationResult.value = Result.success("Lock unlocked")
            }.onFailure { error ->
                _operationResult.value = Result.failure(error)
            }
        }
    }

    fun lockLock(lock: Lock) {
        viewModelScope.launch {
            val result = lockRepository.lockLock(lock)
            result.onSuccess {
                _operationResult.value = Result.success("Lock locked")
            }.onFailure { error ->
                _operationResult.value = Result.failure(error)
            }
        }
    }

    fun getBatteryLevel(lock: Lock) {
        viewModelScope.launch {
            val result = lockRepository.getBatteryLevel(lock)
            result.onSuccess { battery ->
                _operationResult.value = Result.success("Battery: $battery%")
            }.onFailure { error ->
                _operationResult.value = Result.failure(error)
            }
        }
    }

    fun syncLocksFromServer() {
        viewModelScope.launch {
            val result = lockRepository.syncLocksFromServer()
            result.onSuccess { syncedLocks ->
                _operationResult.value = Result.success("Synced ${syncedLocks.size} locks")
            }.onFailure { error ->
                _operationResult.value = Result.failure(error)
            }
        }
    }

    fun deleteLock(lock: Lock) {
        viewModelScope.launch {
            lockRepository.deleteLock(lock)
            _operationResult.value = Result.success("Lock deleted")
        }
    }

    fun getEventsForLock(lockId: Int): LiveData<List<LockEvent>> {
        return lockRepository.getEventsForLock(lockId).asLiveData()
    }
}
