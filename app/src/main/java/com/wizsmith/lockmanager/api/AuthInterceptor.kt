package com.wizsmith.lockmanager.api

import com.wizsmith.lockmanager.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // If access token exists, append as query param (TTLock expects accessToken param for many endpoints)
        val current = SessionManager.accessToken
        val newUrl = if (!current.isNullOrBlank()) {
            request.url.newBuilder()
                .addQueryParameter("accessToken", current)
                .build()
        } else {
            request.url
        }
        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
