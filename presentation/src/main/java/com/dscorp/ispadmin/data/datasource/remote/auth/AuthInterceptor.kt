package com.dscorp.ispadmin.data.datasource.remote.auth

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStore: TokenStore,
    baseUrl: String
) : Interceptor {

    private val backendHost: String? = baseUrl.toHttpUrlOrNull()?.host

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!requiresAuthorization(request.url.host, request.url.encodedPath)) {
            return chain.proceed(request)
        }
        val accessToken = tokenStore.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(request)
        }
        val authorized = request.newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
            .build()
        return chain.proceed(authorized)
    }

    private fun requiresAuthorization(host: String, path: String): Boolean {
        if (backendHost != null && host != backendHost) return false
        if (path.contains(WEBHOOK_SEGMENT)) return false
        return PUBLIC_PATHS.none { path.endsWith(it) }
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        private const val WEBHOOK_SEGMENT = "webhook"
        private val PUBLIC_PATHS = listOf(
            "users/login",
            "users/login/face",
            "users/login/face/photo",
            "users/token/refresh",
            "app/check_version",
            "fcm/save-token"
        )
    }
}
