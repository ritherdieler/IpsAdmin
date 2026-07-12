package com.dscorp.ispadmin.observability

class ObservabilityCrashHandler(
    private val client: ObservabilityClient,
    private val delegate: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        runCatching { client.recordCrash(throwable) }
        delegate?.uncaughtException(thread, throwable)
    }
}
