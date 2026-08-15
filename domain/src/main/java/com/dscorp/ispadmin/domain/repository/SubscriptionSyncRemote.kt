package com.dscorp.ispadmin.domain.repository

sealed class SubscriptionSyncOutcome {
    data object Success : SubscriptionSyncOutcome()
    data object Conflict : SubscriptionSyncOutcome()
    data object IpConflict : SubscriptionSyncOutcome()
    data class Failure(val message: String) : SubscriptionSyncOutcome()
}

interface SubscriptionSyncRemote {
    suspend fun uploadPending(
        subscriptionJson: String,
        clientRequestId: String,
        installationOrderId: Int?,
        facadePhotoFile: java.io.File?
    ): SubscriptionSyncOutcome
}
