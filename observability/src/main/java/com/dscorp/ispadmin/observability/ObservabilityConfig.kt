package com.dscorp.ispadmin.observability

data class ObservabilityConfig(
    val apiKey: String,
    val sanitizePayloads: Boolean = true
)
