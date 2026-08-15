package com.dscorp.ispadmin.data.remote

data class SubscriptionSyncApiResponse(
    val status: Int? = null,
    val error: String? = null,
    val errorCode: String? = null,
    val message: String? = null,
    val data: SubscriptionSyncData? = null
)

data class SubscriptionSyncData(
    val alreadyRegistered: Boolean? = null,
    val provisioningPending: Boolean? = null
)
