package com.example.cleanarchitecture.domain.domain.entity

data class Payment(
    var id: Int? = null,
    var amountPaid: Double = 0.0,
    val discount: Double = 0.0,
    val discountReason:String = "",
    var date: Long = 0,
    var method: String = "",
    var paid: Boolean = false,
    var subscriptionId: Int = 0
)
