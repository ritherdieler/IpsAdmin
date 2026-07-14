package com.dscorp.ispadmin.observability

fun interface ObsUserProvider {
    fun currentUser(): Map<String, Any?>?
}
