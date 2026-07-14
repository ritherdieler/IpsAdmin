package com.dscorp.ispadmin.domain.model

data class PagedSubscriptionResponse(
    val items: List<SubscriptionResponse> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val total: Long = 0,
    val totalPages: Int = 0,
)
