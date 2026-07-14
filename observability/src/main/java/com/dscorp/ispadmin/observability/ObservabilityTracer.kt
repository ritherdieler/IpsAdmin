package com.dscorp.ispadmin.observability

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.concurrent.atomic.AtomicBoolean

class ObservabilityTracer(
    private val api: ObservabilitySpanApi,
    private val queue: ObservabilityEventStore,
    private val contextProvider: ObservabilityContextProvider,
    private val gson: Gson,
    private val apiKey: String,
    private val tagsProvider: () -> Map<String, Any?>? = { null },
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {

    private val scope = coroutineScope
    private val flushing = AtomicBoolean(false)
    private val batchSize = 100
    private val random = SecureRandom()

    fun newTraceId(): String = randomHex(16)

    fun newSpanId(): String = randomHex(8)

    fun traceparent(traceId: String, spanId: String): String = "00-$traceId-$spanId-01"

    fun recordClientSpan(
        traceId: String,
        spanId: String,
        name: String,
        httpMethod: String,
        httpRoute: String,
        httpStatus: Int?,
        startEpochMs: Long,
        durationMs: Long,
        status: String,
        sessionId: String,
        tags: Map<String, Any?>? = null
    ) {
        val enrichedTags = mergeTags(tags, tagsProvider())
        val span = ObsSpanDto(
            traceId = traceId,
            spanId = spanId,
            parentSpanId = null,
            name = name,
            kind = "CLIENT",
            platform = "android",
            sessionId = sessionId,
            startEpochMs = startEpochMs,
            durationMs = durationMs,
            status = status,
            httpMethod = httpMethod,
            httpRoute = httpRoute,
            httpStatus = httpStatus,
            dbStatement = null,
            tagsJson = enrichedTags?.let { runCatching { gson.toJson(it) }.getOrNull() },
            environment = contextProvider.environment(),
            release = contextProvider.release()
        )
        scope.launch {
            runCatching { queue.append(gson.toJson(span)) }
            flushInternal()
        }
    }

    private fun mergeTags(
        explicit: Map<String, Any?>?,
        fromProvider: Map<String, Any?>?
    ): Map<String, Any?>? {
        if (fromProvider.isNullOrEmpty()) return explicit
        if (explicit.isNullOrEmpty()) return fromProvider
        return fromProvider + explicit
    }

    fun flush() {
        scope.launch { flushInternal() }
    }

    private suspend fun flushInternal() {
        if (apiKey.isBlank()) return
        if (!flushing.compareAndSet(false, true)) return
        try {
            while (true) {
                val lines = queue.readAll()
                if (lines.isEmpty()) break
                val consumed = lines.take(batchSize)
                val spans = consumed.mapNotNull {
                    runCatching { gson.fromJson(it, ObsSpanDto::class.java) }.getOrNull()
                }
                if (spans.isEmpty()) {
                    queue.removeFirst(consumed.size)
                    continue
                }
                val response = runCatching { api.sendSpans(apiKey, spans) }.getOrNull() ?: break
                if (response.isSuccessful) {
                    queue.removeFirst(consumed.size)
                    if (consumed.size >= lines.size) break
                } else {
                    break
                }
            }
        } finally {
            flushing.set(false)
        }
    }

    private fun randomHex(bytes: Int): String {
        val buffer = ByteArray(bytes)
        random.nextBytes(buffer)
        val builder = StringBuilder(bytes * 2)
        for (value in buffer) {
            builder.append(Character.forDigit((value.toInt() shr 4) and 0xF, 16))
            builder.append(Character.forDigit(value.toInt() and 0xF, 16))
        }
        return builder.toString()
    }
}
