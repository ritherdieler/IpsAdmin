package com.example.cleanarchitecture.domain.domain.entity

import java.util.*

data class MonthlySubscriptionResume(
    val totalActiveSubscriptions: Int,
    val newSubscriptions: Int,
    val cancelledSubscriptions: Int,
    val date: Date,
)