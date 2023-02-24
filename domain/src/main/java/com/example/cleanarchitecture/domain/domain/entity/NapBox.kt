package com.example.cleanarchitecture.domain.domain.entity

data class NapBox(
    val id: String? = null,
    val code: String = "",
    val address: String = "",
    var location: GeoLocation? = null,

    ) : java.io.Serializable
{
    override fun toString(): String {
        return code
    }
    override fun equals(other: Any?): Boolean {
        return id == (other as NapBox).id
    }
}



