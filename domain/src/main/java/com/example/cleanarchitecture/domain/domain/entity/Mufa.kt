package com.example.cleanarchitecture.domain.domain.entity

data class Mufa(
    var id: Int? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var reference: String? = null,
    var threads: Int? = null,
    var napBoxes: List<NapBox>? = null,
) : java.io.Serializable{
    override fun toString(): String {
        return "$id - $reference"
    }
}
