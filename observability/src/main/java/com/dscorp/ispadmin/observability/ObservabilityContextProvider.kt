package com.dscorp.ispadmin.observability

import android.os.Build

class ObservabilityContextProvider(
    private val appInfo: ObsAppInfo,
    private val userProvider: ObsUserProvider
) {

    fun environment(): String = appInfo.environment()

    fun release(): String = appInfo.release()

    fun userAgent(): String =
        "ispadmin-android/${appInfo.versionName()} (Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"

    fun user(): Map<String, Any?>? = userProvider.currentUser()

    fun device(): Map<String, Any?> = mapOf(
        "manufacturer" to Build.MANUFACTURER,
        "brand" to Build.BRAND,
        "model" to Build.MODEL,
        "osVersion" to Build.VERSION.RELEASE,
        "sdkInt" to Build.VERSION.SDK_INT,
        "flavor" to appInfo.flavor(),
        "appVersion" to appInfo.versionName(),
        "appVersionCode" to appInfo.versionCode()
    )
}
