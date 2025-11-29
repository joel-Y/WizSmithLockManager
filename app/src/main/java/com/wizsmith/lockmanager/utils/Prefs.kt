package com.wizsmith.lockmanager.utils

import android.content.Context

object Prefs {
    private const val PREFS = "wizsmith_prefs"

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREFS, 0).edit()
            .putString("token", token).apply()
    }

    fun getToken(context: Context): String? =
        context.getSharedPreferences(PREFS, 0).getString("token", null)
}
