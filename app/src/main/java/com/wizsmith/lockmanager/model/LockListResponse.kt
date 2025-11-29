package com.wizsmith.lockmanager.model

data class LockListResponse(
    val total: Int = 0,
    val list: List<Lock> = emptyList()
)
