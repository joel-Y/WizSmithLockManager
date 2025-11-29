package com.wizsmith.lockmanager.data.api

import com.wizsmith.lockmanager.data.model.LoginResponse
import retrofit2.http.*

interface TTLockApi {

    @FormUrlEncoded
    @POST("oauth2/token")
    suspend fun login(
        @Field("clientId") clientId: String,
        @Field("clientSecret") clientSecret: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse
}
