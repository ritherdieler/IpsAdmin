package com.dscorp.ispadmin.observability

object ObsSanitize {
    private val SENSITIVE_KEYS = setOf(
        "password",
        "passwd",
        "token",
        "accessToken",
        "refreshToken",
        "authorization",
        "apiKey",
        "secret",
        "dni",
        "creditCard"
    )

    fun sanitizeMap(input: Map<String, Any?>?, sanitize: Boolean): Map<String, Any?>? {
        if (input == null) return null
        if (!sanitize) return input
        return input.mapValues { (key, value) ->
            if (SENSITIVE_KEYS.any { it.equals(key, ignoreCase = true) }) "[redacted]" else value
        }
    }
}
