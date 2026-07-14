package com.dscorp.ispadmin.observability

import okhttp3.Interceptor
import okhttp3.Response

class ObservabilityApiKeyInterceptor(
    private val apiKey: String
) : Interceptor {

    companion object {
        const val HEADER = "X-Obs-Api-Key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (apiKey.isBlank()) {
            return chain.proceed(original)
        }
        val existing = original.header(HEADER)
        if (!existing.isNullOrBlank()) {
            return chain.proceed(original)
        }
        return chain.proceed(
            original.newBuilder()
                .header(HEADER, apiKey)
                .build()
        )
    }
}
