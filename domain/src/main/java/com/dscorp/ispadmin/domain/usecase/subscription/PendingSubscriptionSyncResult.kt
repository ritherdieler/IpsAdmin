package com.dscorp.ispadmin.domain.usecase.subscription

data class PendingSubscriptionSyncResult(
    val syncedCount: Int,
    val failedCount: Int = 0,
    val lastError: String? = null
)
