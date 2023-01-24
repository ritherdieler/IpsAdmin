package com.example.cleanarchitecture.domain.domain.entity

data class SubscriptionResponse(
    val address: String,
    val code: String? = null,
    val dni: String,
    val firstName: String,
    val id: String? = null,
    val lastName: String,
    val location: GeoLocation? = null,
    val napBox: NapBox? = null,
    val networkDevice: NetworkDevice ? = null,
    val new: Boolean,
    val password: String,
    val phone: String,
    val place: Place? = null,
    val plan: Plan? = null,
    val serviceIsSuspended: Boolean,
    val technician: Technician? = null
):java.io.Serializable
