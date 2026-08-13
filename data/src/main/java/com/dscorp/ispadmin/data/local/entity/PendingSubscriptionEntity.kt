package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus

@Entity(
    tableName = "pending_subscriptions",
    indices = [Index(value = ["clientRequestId"], unique = true)]
)
data class PendingSubscriptionEntity(
    @PrimaryKey val localId: String,
    val clientRequestId: String,
    val subscriptionJson: String,
    val facadePhotoPath: String?,
    val installationOrderId: Int?,
    val status: PendingSubscriptionStatus,
    val createdAt: Long,
    val lastError: String?,
    val retryCount: Int
)
