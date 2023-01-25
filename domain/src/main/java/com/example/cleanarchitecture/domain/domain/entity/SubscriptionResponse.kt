package com.example.cleanarchitecture.domain.domain.entity

data class SubscriptionResponse(
    var address: String? = null,
    var code: String? = null,
    var dni: String? = null,
    var firstName: String? = null,
    var id: String? = null,
    var lastName: String? = null,
    var location: GeoLocation? = null,
    var napBox: NapBox? = null,
    var networkDevice: NetworkDevice ? = null,
    var new: Boolean? = null,
    var password: String? = null,
    var phone: String? = null,
    var place: Place? = null,
    var plan: Plan? = null,
    var serviceIsSuspended: Boolean? = null,
    var technician: Technician? = null
):java.io.Serializable
