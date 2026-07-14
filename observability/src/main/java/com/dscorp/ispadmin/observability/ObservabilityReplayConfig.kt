package com.dscorp.ispadmin.observability

data class ObservabilityReplayConfig(
    val enableReplay: Boolean = true,
    val replayFps: Int = 2,
    val replayWindowMs: Long = 10000L,
    val maxReplaysPerSession: Int = 10,
    val replayMinIntervalMs: Long = 10_000L,
    val replayMaxBytes: Long = 15L * 1024 * 1024,
    val replayWidthPx: Int = 400
) {
    val captureIntervalMs: Long
        get() = if (replayFps <= 0) 1000L else (1000L / replayFps).coerceAtLeast(1L)
}
