package com.dscorp.ispadmin.observability

import android.os.Build
import com.dscorp.ispadmin.BuildConfig
import com.dscorp.ispadmin.data.repository.IRepository

class ObservabilityContextProvider(
    private val repository: IRepository
) {

    fun environment(): String = when (BuildConfig.FLAVOR) {
        "prod" -> "prod"
        "dev" -> "dev"
        else -> "local"
    }

    fun release(): String = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    fun userAgent(): String =
        "ispadmin-android/${BuildConfig.VERSION_NAME} (Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"

    fun user(): Map<String, Any?>? {
        val session = runCatching { repository.getUserSession() }.getOrNull() ?: return null
        return mapOf(
            "id" to session.id,
            "username" to session.username,
            "name" to session.toString(),
            "email" to session.email,
            "type" to session.typeAsString()
        )
    }

    fun device(): Map<String, Any?> = mapOf(
        "manufacturer" to Build.MANUFACTURER,
        "brand" to Build.BRAND,
        "model" to Build.MODEL,
        "osVersion" to Build.VERSION.RELEASE,
        "sdkInt" to Build.VERSION.SDK_INT,
        "flavor" to BuildConfig.FLAVOR,
        "appVersion" to BuildConfig.VERSION_NAME,
        "appVersionCode" to BuildConfig.VERSION_CODE
    )
}
