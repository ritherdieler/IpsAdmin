package com.dscorp.ispadmin.observability

data class ObsSpanDto(
    val traceId: String,
    val spanId: String,
    val parentSpanId: String? = null,
    val name: String,
    val kind: String = "CLIENT",
    val platform: String = "android",
    val sessionId: String,
    val startEpochMs: Long,
    val durationMs: Long,
    val status: String,
    val httpMethod: String? = null,
    val httpRoute: String? = null,
    val httpStatus: Int? = null,
    val dbStatement: String? = null,
    val tagsJson: String? = null,
    val environment: String,
    val release: String? = null
)
