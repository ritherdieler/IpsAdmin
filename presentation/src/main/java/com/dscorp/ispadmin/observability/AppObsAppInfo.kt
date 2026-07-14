package com.dscorp.ispadmin.observability

import com.dscorp.ispadmin.BuildConfig

class AppObsAppInfo : ObsAppInfo {
    override fun environment(): String = when (BuildConfig.FLAVOR) {
        "prod" -> "prod"
        "dev" -> "dev"
        else -> "local"
    }

    override fun release(): String = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    override fun versionName(): String = BuildConfig.VERSION_NAME

    override fun versionCode(): Int = BuildConfig.VERSION_CODE

    override fun flavor(): String = BuildConfig.FLAVOR
}
