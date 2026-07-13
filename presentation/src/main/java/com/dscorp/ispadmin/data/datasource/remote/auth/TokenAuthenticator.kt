package com.dscorp.ispadmin.data.datasource.remote.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val authApiService: AuthApiService,
    private val sessionEventBus: SessionEventBus
) : Authenticator {

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenStore.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            forceLogout()
            return null
        }

        val failedToken = response.request
            .header(AuthInterceptor.HEADER_AUTHORIZATION)
            ?.removePrefix(AuthInterceptor.BEARER_PREFIX)
            ?.trim()
        val currentToken = tokenStore.getAccessToken()

        if (!currentToken.isNullOrBlank() && currentToken != failedToken) {
            return response.request.newBuilder()
                .header(
                    AuthInterceptor.HEADER_AUTHORIZATION,
                    "${AuthInterceptor.BEARER_PREFIX}$currentToken"
                )
                .build()
        }

        if (responseCount(response) >= MAX_ATTEMPTS) {
            forceLogout()
            return null
        }

        val refreshed = runCatching {
            authApiService.refreshToken(RefreshTokenRequest(refreshToken)).execute()
        }.getOrNull()

        if (refreshed == null || !refreshed.isSuccessful) {
            forceLogout()
            return null
        }

        val newAccessToken = refreshed.body()?.accessToken
        if (newAccessToken.isNullOrBlank()) {
            forceLogout()
            return null
        }

        val newRefreshToken = refreshed.body()?.refreshToken ?: refreshToken
        tokenStore.saveTokens(newAccessToken, newRefreshToken)

        return response.request.newBuilder()
            .header(
                AuthInterceptor.HEADER_AUTHORIZATION,
                "${AuthInterceptor.BEARER_PREFIX}$newAccessToken"
            )
            .build()
    }

    private fun forceLogout() {
        tokenStore.clearSession()
        sessionEventBus.notifySessionExpired()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    companion object {
        private const val MAX_ATTEMPTS = 2
    }
}
