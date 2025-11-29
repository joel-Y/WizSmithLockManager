package com.wizsmith.lockmanager.util

import android.content.Context
import android.content.SharedPreferences
import com.wizsmith.lockmanager.BuildConfig

object SessionManager {
    private const val PREFS = "wizsmith_prefs"
    private const val KEY_TOKEN = "key_access_token"
    private const val KEY_ROLE = "key_role"
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    var accessToken: String?
        get() = prefs?.getString(KEY_TOKEN, null)
        set(value) { prefs?.edit()?.putString(KEY_TOKEN, value)?.apply() }

    var userRole: String?
        get() = prefs?.getString(KEY_ROLE, null)
        set(value) { prefs?.edit()?.putString(KEY_ROLE, value)?.apply() }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }

    // Convenience to read clientId/secret from BuildConfig
    val clientId: String get() = BuildConfig.TTLOCK_CLIENT_ID
    val clientSecret: String get() = BuildConfig.TTLOCK_CLIENT_SECRET
}
