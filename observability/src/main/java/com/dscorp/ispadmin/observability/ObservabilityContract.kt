package com.dscorp.ispadmin.observability

data class ObsEventDto(
    val eventType: String,
    val platform: String = "android",
    val severity: String,
    val message: String? = null,
    val errorType: String? = null,
    val stacktrace: String? = null,
    val environment: String,
    val release: String? = null,
    val correlationId: String? = null,
    val sessionId: String? = null,
    val url: String? = null,
    val httpMethod: String? = null,
    val httpStatus: Int? = null,
    val durationMs: Long? = null,
    val userAgent: String? = null,
    val user: Map<String, Any?>? = null,
    val device: Map<String, Any?>? = null,
    val breadcrumbs: List<Map<String, Any?>>? = null,
    val tags: Map<String, Any?>? = null,
    val context: Map<String, Any?>? = null,
    val replayId: Long? = null,
    val timestamp: Long
)

data class ObsBatchRequest(
    val events: List<ObsEventDto>
)

data class ObsBatchResponse(
    val accepted: Int = 0,
    val rejected: Int = 0,
    val issueIds: List<Long> = emptyList()
)

data class ObsReplayManifest(
    val format: String,
    val width: Int,
    val height: Int,
    val frames: List<ObsReplayFrame>,
    val durationMs: Long
)

data class ObsReplayUploadResponse(
    val id: Long? = null,
    val sessionId: String? = null,
    val format: String? = null,
    val sizeBytes: Long? = null,
    val contentEncoding: String? = null
)
