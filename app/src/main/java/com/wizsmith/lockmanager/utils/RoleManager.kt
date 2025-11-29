package com.wizsmith.lockmanager.utils

object RoleManager {
    fun isAdmin(role: String) = role.lowercase() == "admin"
}
