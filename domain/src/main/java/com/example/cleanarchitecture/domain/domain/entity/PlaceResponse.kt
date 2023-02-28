package com.example.cleanarchitecture.domain.domain.entity

data class PlaceResponse(
    val id: String? = null,
    val abbreviation: String? = null ,
    val name: String? = null ,
    val location: GeoLocation? = null,
)
{
    override fun toString(): String {
        return name!!
    }
}
