package com.example.cleanarchitecture.domain.domain.entity

data class SubscriptionResponse(
    val address: String,
    val code: Int?,
    val dni: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val networkDevice: NetworkDevice,
    val new: Boolean,
    val password: String,
    val phone: String,
    val place: Place,
    val plan: Plan,
    val serviceOrder: ServiceOrder,
    val serviceIsSuspended: Boolean
)