package com.dscorp.ispadmin.data.datasource.remote.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RefreshTokenRequest(val refreshToken: String)

data class RefreshTokenResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null
)

interface AuthApiService {
    @POST("users/token/refresh")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<RefreshTokenResponse>
}
