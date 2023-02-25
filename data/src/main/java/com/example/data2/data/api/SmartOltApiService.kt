package com.example.data2.data.api

import com.example.data2.data.response.UnconfirmedOnusResponse
import retrofit2.Response
import retrofit2.http.GET

interface SmartOltApiService {

    @GET("onu/unconfigured_onus")
    suspend fun getUnconfirmedOnus():Response<UnconfirmedOnusResponse>

}