package com.dscorp.ispadmin.domain.connectivity

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityMonitor {
    fun isConnected(): Boolean
    fun observeConnectivity(): Flow<Boolean>
}
