package com.dscorp.ispadmin.domain.connectivity

interface MikrotikReachabilityMonitor {
    suspend fun isMikrotikReachable(): Boolean
}
