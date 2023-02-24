package com.example.cleanarchitecture.domain.domain.entity

data class NapBoxResponse(
    var id: String? = null,
    var code: String? = null,
    var address: String? = null,
    var location: GeoLocation? = null,
):java.io.Serializable
