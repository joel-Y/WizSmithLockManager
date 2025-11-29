package com.wizsmith.lockmanager.data.repository

import com.ttlock.bl.sdk.api.TTLockClient
import com.ttlock.bl.sdk.callback.*
import com.ttlock.bl.sdk.entity.LockError
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice
import com.wizsmith.lockmanager.data.local.dao.LockDao
import com.wizsmith.lockmanager.data.local.dao.LockEventDao
import com.wizsmith.lockmanager.data.models.Lock
import com.wizsmith.lockmanager.data.models.LockEvent
import com.wizsmith.lockmanager.data.remote.ApiClient
import com.wizsmith.lockmanager.utils.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LockRepository @Inject constructor(
    private val lockDao: LockDao,
    private val lockEventDao: LockEventDao,
    private val apiClient: ApiClient,
    private val preferenceManager: PreferenceManager
) {

    // Local database operations
    fun getAllLocks(): Flow<List<Lock>> = lockDao.getAllLocks()

    suspend fun getLockById(lockId: Int): Lock? = lockDao.getLockById(lockId)

    suspend fun insertLock(lock: Lock) = lockDao.insertLock(lock)

    suspend fun updateLock(lock: Lock) = lockDao.updateLock(lock)

    suspend fun deleteLock(lock: Lock) {
        lockDao.deleteLock(lock)
        lockEventDao.deleteEventsForLock(lock.lockId)
    }

    // Event logging
    suspend fun logEvent(lockId: Int, eventType: String, detail: String) {
        val event = LockEvent(
            lockId = lockId,
            eventType = eventType,
            eventDetail = detail,
            username = preferenceManager.getUsername()
        )
        lockEventDao.insertEvent(event)
    }

    fun getEventsForLock(lockId: Int): Flow<List<LockEvent>> = 
        lockEventDao.getEventsForLock(lockId)

    // TTLock SDK operations
    suspend fun scanForLocks(): Result<List<ExtendedBluetoothDevice>> = 
        suspendCancellableCoroutine { continuation ->
            val devices = mutableListOf<ExtendedBluetoothDevice>()
            
            TTLockClient.getDefault().startScanLock(object : ScanLockCallback() {
                override fun onScanLockSuccess(device: ExtendedBluetoothDevice) {
                    if (!devices.any { it.address == device.address }) {
                        devices.add(device)
                    }
                }

                override fun onFail(error: LockError) {
                    continuation.resume(Result.failure(Exception(error.errorMsg)))
                }
            })

            continuation.invokeOnCancellation {
                TTLockClient.getDefault().stopScanLock()
            }

            // Return devices after 5 seconds
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                TTLockClient.getDefault().stopScanLock()
                continuation.resume(Result.success(devices))
            }, 5000)
        }

    suspend fun initializeLock(
        device: ExtendedBluetoothDevice,
        lockName: String
    ): Result<Lock> = suspendCancellableCoroutine { continuation ->
        
        TTLockClient.getDefault().initLock(device, object : InitLockCallback() {
            override fun onInitLockSuccess(lockData: String) {
                val lock = Lock(
                    lockId = System.currentTimeMillis().toInt(),
                    lockName = lockName,
                    lockMac = device.address,
                    lockData = lockData,
                    electricQuantity = device.battery ?: 100
                )
                continuation.resume(Result.success(lock))
            }

            override fun onFail(error: LockError) {
                continuation.resume(Result.failure(Exception(error.errorMsg)))
            }
        })
    }

    suspend fun unlockLock(lock: Lock): Result<Unit> = 
        suspendCancellableCoroutine { continuation ->
            
            TTLockClient.getDefault().unlock(lock.lockData, object : ControlLockCallback() {
                override fun onControlLockSuccess(
                    lockAction: Int,
                    battery: Int,
                    uniqueId: Int
                ) {
                    // Update lock state
                    kotlinx.coroutines.GlobalScope.launch {
                        val updatedLock = lock.copy(
                            isLocked = false,
                            electricQuantity = battery,
                            lastUpdateTime = System.currentTimeMillis()
                        )
                        updateLock(updatedLock)
                        logEvent(lock.lockId, "unlock", "Lock unlocked successfully")
                    }
                    continuation.resume(Result.success(Unit))
                }

                override fun onFail(error: LockError) {
                    continuation.resume(Result.failure(Exception(error.errorMsg)))
                }
            })
        }

    suspend fun lockLock(lock: Lock): Result<Unit> = 
        suspendCancellableCoroutine { continuation ->
            
            TTLockClient.getDefault().lock(lock.lockData, object : ControlLockCallback() {
                override fun onControlLockSuccess(
                    lockAction: Int,
                    battery: Int,
                    uniqueId: Int
                ) {
                    kotlinx.coroutines.GlobalScope.launch {
                        val updatedLock = lock.copy(
                            isLocked = true,
                            electricQuantity = battery,
                            lastUpdateTime = System.currentTimeMillis()
                        )
                        updateLock(updatedLock)
                        logEvent(lock.lockId, "lock", "Lock locked successfully")
                    }
                    continuation.resume(Result.success(Unit))
                }

                override fun onFail(error: LockError) {
                    continuation.resume(Result.failure(Exception(error.errorMsg)))
                }
            })
        }

    suspend fun getBatteryLevel(lock: Lock): Result<Int> = 
        suspendCancellableCoroutine { continuation ->
            
            TTLockClient.getDefault().getElectricQuantity(
                lock.lockData,
                object : GetElectricQuantityCallback() {
                    override fun onGetElectricQuantitySuccess(electricQuantity: Int) {
                        kotlinx.coroutines.GlobalScope.launch {
                            val updatedLock = lock.copy(
                                electricQuantity = electricQuantity,
                                lastUpdateTime = System.currentTimeMillis()
                            )
                            updateLock(updatedLock)
                        }
                        continuation.resume(Result.success(electricQuantity))
                    }

                    override fun onFail(error: LockError) {
                        continuation.resume(Result.failure(Exception(error.errorMsg)))
                    }
                }
            )
        }

    // API operations
    suspend fun syncLocksFromServer(): Result<List<Lock>> {
        return try {
            val token = preferenceManager.getAccessToken() ?: return Result.failure(
                Exception("Not authenticated")
            )
            
            val response = apiClient.ttLockApi.getLockList("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                val lockInfos = response.body()!!.list
                val locks = lockInfos.map { info ->
                    Lock(
                        lockId = info.lockId,
                        lockName = info.lockAlias.ifEmpty { info.lockName },
                        lockMac = info.lockMac,
                        lockData = info.lockData,
                        electricQuantity = info.electricQuantity,
                        lastUpdateTime = info.updateDate
                    )
                }
                
                lockDao.insertLocks(locks)
                Result.success(locks)
            } else {
                Result.failure(Exception("Failed to fetch locks: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
