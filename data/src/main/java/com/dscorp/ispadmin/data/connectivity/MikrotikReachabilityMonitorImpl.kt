package com.dscorp.ispadmin.data.connectivity

import com.dscorp.ispadmin.domain.connectivity.MikrotikReachabilityMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class MikrotikReachabilityMonitorImpl(
    okHttpClient: OkHttpClient,
    private val baseUrl: String,
    timeoutMs: Long = DEFAULT_TIMEOUT_MS
) : MikrotikReachabilityMonitor {

    private val pingClient: OkHttpClient = okHttpClient.newBuilder()
        .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .callTimeout(timeoutMs + 500, TimeUnit.MILLISECONDS)
        .authenticator { _, _ -> null }
        .build()

    override suspend fun isMikrotikReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(pingUrl())
                .get()
                .build()
            pingClient.newCall(request).execute().use { true }
        } catch (_: IOException) {
            false
        }
    }

    private fun pingUrl(): String {
        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return normalized + PING_PATH
    }

    companion object {
        const val DEFAULT_TIMEOUT_MS = 3000L
        const val PING_PATH = "subscription/provisioning-ready"
    }
}
