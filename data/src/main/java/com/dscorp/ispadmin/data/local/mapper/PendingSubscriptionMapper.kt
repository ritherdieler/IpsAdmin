package com.dscorp.ispadmin.data.local.mapper

import com.dscorp.ispadmin.data.local.entity.PendingSubscriptionEntity
import com.dscorp.ispadmin.domain.model.PendingSubscription

fun PendingSubscriptionEntity.toDomain(): PendingSubscription = PendingSubscription(
    localId = localId,
    clientRequestId = clientRequestId,
    subscriptionJson = subscriptionJson,
    facadePhotoPath = facadePhotoPath,
    installationOrderId = installationOrderId,
    status = status,
    createdAt = createdAt,
    lastError = lastError,
    retryCount = retryCount
)

fun PendingSubscription.toEntity(): PendingSubscriptionEntity = PendingSubscriptionEntity(
    localId = localId,
    clientRequestId = clientRequestId,
    subscriptionJson = subscriptionJson,
    facadePhotoPath = facadePhotoPath,
    installationOrderId = installationOrderId,
    status = status,
    createdAt = createdAt,
    lastError = lastError,
    retryCount = retryCount
)
