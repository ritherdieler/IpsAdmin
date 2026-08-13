package com.dscorp.ispadmin.domain.model

data class PendingSubscription(
    val localId: String,
    val clientRequestId: String,
    val subscriptionJson: String,
    val facadePhotoPath: String? = null,
    val installationOrderId: Int? = null,
    val status: PendingSubscriptionStatus = PendingSubscriptionStatus.PENDING,
    val createdAt: Long,
    val lastError: String? = null,
    val retryCount: Int = 0
)
