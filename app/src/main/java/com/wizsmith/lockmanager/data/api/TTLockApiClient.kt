package com.wizsmith.lockmanager.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TTLockApiClient {

    private const val BASE_URL = "https://api.ttlock.com.cn/"

    private val client = OkHttpClient.Builder().build()

    val api: TTLockApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TTLockApi::class.java)
}
