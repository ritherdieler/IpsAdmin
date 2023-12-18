package com.example.cleanarchitecture.domain.domain.entity

data class SubscriptionResume(
    val id: Int,
    val planName: String,
    val customerName: String,
    val antiquity: String,
    val qualification: String,
    val placeName: String,
    val ics: String,
    val lastPaymentDate: String,
    val pendingInvoicesQuantity: Int,
    val totalDebt: Double,
    val customer:CustomerData
)