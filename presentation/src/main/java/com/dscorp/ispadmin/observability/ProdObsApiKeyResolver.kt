package com.dscorp.ispadmin.observability

object ProdObsApiKeyResolver {
    fun resolve(androidKey: String, legacyKey: String = ""): String {
        val trimmedAndroid = androidKey.trim()
        if (trimmedAndroid.isNotEmpty()) return trimmedAndroid
        val trimmedLegacy = legacyKey.trim()
        if (trimmedLegacy.isNotEmpty()) return trimmedLegacy
        return ""
    }
}
