package com.example.cleanarchitecture.domain.domain.entity

data class Mufa(
    var id: Int? = null,
    var latitude: Float? = null,
    var longitude: Float? = null,
    var reference: String? = null,
    var threads: Int? = null
) : java.io.Serializable{
    override fun toString(): String {
        return "$id - $reference"
    }
}
