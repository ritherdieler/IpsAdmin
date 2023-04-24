package com.example.cleanarchitecture.domain.domain.entity

data class NapBoxResponse(
    val id: String? = null,
    val code: String = "",
    val address: String = "",
    val mufaId: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ports_number: Int? = null,
):java.io.Serializable
{
    override fun toString(): String {
        return "$code - $address"
    }
}