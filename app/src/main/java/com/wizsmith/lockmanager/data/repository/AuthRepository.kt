package com.wizsmith.lockmanager.data.repository

import com.wizsmith.lockmanager.BuildConfig
import com.wizsmith.lockmanager.data.api.TTLockApiClient

class AuthRepository {

    suspend fun login(username: String, password: String) =
        TTLockApiClient.api.login(
            BuildConfig.TTLOCK_CLIENT_ID,
            BuildConfig.TTLOCK_CLIENT_SECRET,
            username,
            password
        )
}
