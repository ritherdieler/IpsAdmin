package com.example.cleanarchitecture.domain.domain.entity

data class CustomerData(
    val subscriptionId: Int,
    var name: String,
    var lastName: String,
    var dni: String,
    var place: String,
    var address: String,
    var phone: String,
    var email: String,
)