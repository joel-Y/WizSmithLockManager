package com.wizsmith.lockmanager.data.model

data class LoginResponse(
    val access_token: String,
    val refresh_token: String,
    val uid: Int,
    val scope: String,
    val expires_in: Int
)
