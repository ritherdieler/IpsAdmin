package com.example.data2.data.repository

import com.example.data2.data.api.SmartOltApiService
import com.example.data2.data.response.Onu

class OltRepository(private val oltApiService: SmartOltApiService) : IOltRepository {
    override suspend fun getUnconfirmedOnus(): List<Onu> {
        val response = oltApiService.getUnconfirmedOnus()
        return if (response.code() == 200) response.body()!!.response
        else throw Exception("Error generico")
    }
}