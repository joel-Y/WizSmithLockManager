package com.wizsmith.lockmanager.model

data class UserDetailResponse(
    val uid: Long?,
    val username: String?,
    val role: String? // "admin" or "technician" (assumes API returns role)
)
