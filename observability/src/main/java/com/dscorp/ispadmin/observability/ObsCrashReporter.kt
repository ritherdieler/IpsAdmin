package com.dscorp.ispadmin.observability

fun interface ObsCrashReporter {
    fun recordException(throwable: Throwable)
}
