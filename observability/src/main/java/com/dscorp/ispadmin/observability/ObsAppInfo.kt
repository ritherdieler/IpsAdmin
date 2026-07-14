package com.dscorp.ispadmin.observability

interface ObsAppInfo {
    fun environment(): String
    fun release(): String
    fun versionName(): String
    fun versionCode(): Int
    fun flavor(): String
}
