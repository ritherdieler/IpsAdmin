package com.dscorp.ispadmin.observability

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.io.PrintWriter
import java.io.StringWriter
import java.util.ArrayDeque
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class ObservabilityClient(
    private val api: ObservabilityApi,
    private val queue: ObservabilityQueue,
    private val contextProvider: ObservabilityContextProvider,
    private val gson: Gson,
    private val apiKey: String,
    private val workScheduler: ObservabilityWorkScheduler,
    private val replaySender: ObservabilityReplaySender? = null
) {

    private val sessionIdRef = AtomicReference(UUID.randomUUID().toString())
    private val lastBackgroundAt = AtomicLong(0L)
    private val sessionTimeoutMs = TimeUnit.MINUTES.toMillis(30)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val breadcrumbs = ArrayDeque<Map<String, Any?>>()
    private val breadcrumbLock = Any()
    private val maxBreadcrumbs = 50
    private val flushing = AtomicBoolean(false)
    private val batchSize = 100

    fun currentSessionId(): String = sessionIdRef.get()

    fun start() {
        registerLifecycleObserver()
        flush()
    }

    private fun registerLifecycleObserver() {
        runCatching {
            ProcessLifecycleOwner.get().lifecycle.addObserver(
                object : DefaultLifecycleObserver {
                    override fun onStop(owner: LifecycleOwner) {
                        lastBackgroundAt.set(System.currentTimeMillis())
                    }

                    override fun onStart(owner: LifecycleOwner) {
                        val backgroundedAt = lastBackgroundAt.get()
                        if (backgroundedAt > 0 &&
                            System.currentTimeMillis() - backgroundedAt > sessionTimeoutMs
                        ) {
                            sessionIdRef.set(UUID.randomUUID().toString())
                        }
                    }
                }
            )
        }
    }

    fun addBreadcrumb(category: String, message: String, data: Map<String, Any?>? = null) {
        synchronized(breadcrumbLock) {
            breadcrumbs.addLast(
                mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "category" to category,
                    "message" to message,
                    "data" to data
                )
            )
            while (breadcrumbs.size > maxBreadcrumbs) breadcrumbs.removeFirst()
        }
    }

    fun reportError(
        throwable: Throwable,
        message: String? = null,
        severity: String = "error",
        tags: Map<String, Any?>? = null
    ) {
        runCatching { FirebaseCrashlytics.getInstance().recordException(throwable) }
        scope.launch {
            val replayId = runCatching { replaySender?.captureAndUpload(currentSessionId()) }.getOrNull()
            val event = buildEvent(
                eventType = "error",
                severity = severity,
                message = message ?: throwable.message,
                errorType = throwable.javaClass.name,
                stacktrace = stackTraceToString(throwable),
                tags = tags,
                replayId = replayId
            )
            runCatching { queue.append(gson.toJson(event)) }
            flushInternal()
        }
        workScheduler.scheduleEventFlush()
    }

    fun reportLog(message: String, severity: String = "info", tags: Map<String, Any?>? = null) {
        enqueue(
            buildEvent(
                eventType = "log",
                severity = severity,
                message = message,
                tags = tags
            )
        )
    }

    fun reportHttpError(
        url: String,
        httpMethod: String,
        httpStatus: Int,
        durationMs: Long,
        correlationId: String,
        message: String? = null,
        stacktrace: String? = null
    ) {
        enqueue(
            buildEvent(
                eventType = "http_error",
                severity = if (httpStatus >= 500) "error" else "warning",
                message = message ?: "HTTP $httpStatus $httpMethod $url",
                errorType = "HttpError",
                stacktrace = stacktrace,
                url = url,
                httpMethod = httpMethod,
                httpStatus = httpStatus,
                durationMs = durationMs,
                correlationId = correlationId
            )
        )
        workScheduler.scheduleEventFlush()
    }

    fun recordCrash(throwable: Throwable) {
        val replayId = runCatching {
            runBlocking {
                withTimeoutOrNull(CRASH_REPLAY_TIMEOUT_MS) {
                    replaySender?.captureAndUpload(currentSessionId())
                }
            }
        }.getOrNull()
        val event = buildEvent(
            eventType = "crash",
            severity = "fatal",
            message = throwable.message ?: throwable.javaClass.name,
            errorType = throwable.javaClass.name,
            stacktrace = stackTraceToString(throwable),
            replayId = replayId
        )
        runCatching { queue.append(gson.toJson(event)) }
        runCatching { workScheduler.scheduleEventFlush() }
    }

    fun flush() {
        scope.launch { flushInternal() }
    }

    private fun enqueue(event: ObsEventDto) {
        scope.launch {
            runCatching { queue.append(gson.toJson(event)) }
            flushInternal()
        }
    }

    private suspend fun flushInternal() {
        if (apiKey.isBlank()) return
        if (!flushing.compareAndSet(false, true)) return
        try {
            ObservabilityEventSender.flush(api, queue, gson, apiKey, batchSize)
        } finally {
            flushing.set(false)
        }
    }

    private fun buildEvent(
        eventType: String,
        severity: String,
        message: String? = null,
        errorType: String? = null,
        stacktrace: String? = null,
        url: String? = null,
        httpMethod: String? = null,
        httpStatus: Int? = null,
        durationMs: Long? = null,
        correlationId: String? = null,
        tags: Map<String, Any?>? = null,
        replayId: Long? = null
    ): ObsEventDto = ObsEventDto(
        eventType = eventType,
        severity = severity,
        message = message,
        errorType = errorType,
        stacktrace = stacktrace,
        environment = contextProvider.environment(),
        release = contextProvider.release(),
        correlationId = correlationId,
        sessionId = currentSessionId(),
        url = url,
        httpMethod = httpMethod,
        httpStatus = httpStatus,
        durationMs = durationMs,
        userAgent = contextProvider.userAgent(),
        user = contextProvider.user(),
        device = contextProvider.device(),
        breadcrumbs = currentBreadcrumbs(),
        tags = tags,
        context = null,
        replayId = replayId,
        timestamp = System.currentTimeMillis()
    )

    private fun currentBreadcrumbs(): List<Map<String, Any?>> =
        synchronized(breadcrumbLock) { breadcrumbs.toList() }

    private fun stackTraceToString(throwable: Throwable): String {
        val writer = StringWriter()
        throwable.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }

    companion object {
        private const val CRASH_REPLAY_TIMEOUT_MS = 2500L
    }
}
