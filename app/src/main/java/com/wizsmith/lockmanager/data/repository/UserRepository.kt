package com.wizsmith.lockmanager.data.repository

import com.wizsmith.lockmanager.BuildConfig
import com.wizsmith.lockmanager.data.local.dao.UserDao
import com.wizsmith.lockmanager.data.models.User
import com.wizsmith.lockmanager.data.remote.ApiClient
import com.wizsmith.lockmanager.utils.PreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiClient: ApiClient,
    private val preferenceManager: PreferenceManager
) {

    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUser()

    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = apiClient.ttLockApi.login(
                clientId = BuildConfig.TTLOCK_CLIENT_ID,
                clientSecret = BuildConfig.TTLOCK_CLIENT_SECRET,
                username = username,
                password = password
            )

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                
                val user = User(
                    userId = loginResponse.uid.toString(),
                    username = username,
                    accessToken = loginResponse.access_token,
                    refreshToken = loginResponse.refresh_token
                )

                // Save to database
                userDao.deleteAllUsers()
                userDao.insertUser(user)

                // Save to preferences
                preferenceManager.saveAccessToken(loginResponse.access_token)
                preferenceManager.saveUserId(user.userId)
                preferenceManager.saveUsername(username)
                preferenceManager.setLoggedIn(true)

                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userDao.deleteAllUsers()
        preferenceManager.clearAll()
    }

    fun isLoggedIn(): Boolean = preferenceManager.isLoggedIn()
}
