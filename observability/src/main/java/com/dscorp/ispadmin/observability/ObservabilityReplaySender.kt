package com.dscorp.ispadmin.observability

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.zip.GZIPOutputStream

class ObservabilityReplaySender(
    private val api: ObservabilityApi,
    private val recorder: ObservabilityScreenRecorder,
    private val gson: Gson,
    private val apiKey: String,
    private val config: ObservabilityReplayConfig
) {

    private val sessionRef = AtomicReference<String?>(null)
    private val replaysInSession = AtomicInteger(0)
    private val lastReplayAt = AtomicLong(0L)

    suspend fun captureAndUpload(sessionId: String, workflowId: String? = null): Long? {
        if (!config.enableReplay || apiKey.isBlank()) return null
        if (!reserveSlot(sessionId)) return null

        val snapshot = recorder.snapshot()
        if (snapshot == null || snapshot.frames.isEmpty()) {
            releaseSlot()
            return null
        }

        val manifest = ObsReplayManifest(
            format = FORMAT,
            width = snapshot.width,
            height = snapshot.height,
            frames = snapshot.frames,
            durationMs = snapshot.durationMs
        )
        val payload = gzip(gson.toJson(manifest).toByteArray(Charsets.UTF_8))
        if (payload.size > config.replayMaxBytes) {
            releaseSlot()
            return null
        }

        val body = payload.toRequestBody(JSON_MEDIA_TYPE)
        val response = runCatching {
            api.uploadReplay(
                apiKey = apiKey,
                contentEncoding = "gzip",
                format = FORMAT,
                sessionId = sessionId,
                durationMs = snapshot.durationMs,
                workflowId = workflowId?.takeIf { it.isNotBlank() },
                body = body
            )
        }.getOrNull()

        if (response == null || !response.isSuccessful) {
            releaseSlot()
            return null
        }

        recorder.clear()
        return response.body()?.id
    }

    @Synchronized
    private fun reserveSlot(sessionId: String): Boolean {
        if (sessionRef.get() != sessionId) {
            sessionRef.set(sessionId)
            replaysInSession.set(0)
            lastReplayAt.set(0L)
        }
        if (replaysInSession.get() >= config.maxReplaysPerSession) return false
        val now = System.currentTimeMillis()
        val last = lastReplayAt.get()
        if (last > 0 && now - last < config.replayMinIntervalMs) return false
        replaysInSession.incrementAndGet()
        lastReplayAt.set(now)
        return true
    }

    @Synchronized
    private fun releaseSlot() {
        if (replaysInSession.get() > 0) {
            replaysInSession.decrementAndGet()
        }
        lastReplayAt.set(0L)
    }

    private fun gzip(data: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { it.write(data) }
        return outputStream.toByteArray()
    }

    companion object {
        private const val FORMAT = "frames"
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}
