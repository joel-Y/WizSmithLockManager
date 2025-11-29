package com.wizsmith.lockmanager.api

import com.wizsmith.lockmanager.model.LoginResponse
import com.wizsmith.lockmanager.model.LockListResponse
import com.wizsmith.lockmanager.model.UserDetailResponse
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("oauth2/token")
    suspend fun login(
        @Field("clientId") clientId: String,
        @Field("clientSecret") clientSecret: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password"
    ): LoginResponse

    // user detail (example endpoint)
    @GET("v3/user/detail")
    suspend fun getUserDetail(): UserDetailResponse

    // sample locks list
    @GET("v3/lock/list")
    suspend fun getLocks(
        @Query("pageNo") pageNo: Int = 1,
        @Query("pageSize") pageSize: Int = 100
    ): LockListResponse
}
