package com.example.data2.data.repository

import com.example.data2.data.response.Onu

interface IOltRepository {
    suspend fun getUnconfirmedOnus(): List<Onu>
}