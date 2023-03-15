package com.example.cleanarchitecture.domain.domain.entity

data class PlaceResponse(
    val id: String? = null,
    val name: String? = null ,
    val latitude: Float? = null,
    val longitude: Float? = null,
)
{
    override fun toString(): String {
        return name!!
    }
}
