package com.dscorp.ispadmin.domain.model

data class SubscriptionRegistrationRequest(
    val subscriptionJson: String,
    val clientRequestId: String? = null,
    val installationOrderId: Int? = null
)
