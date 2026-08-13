package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.connectivity.MikrotikReachabilityMonitor
import com.dscorp.ispadmin.domain.connectivity.NetworkConnectivityMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class ObserveOfflineRegistrationModeUseCase(
    private val connectivityMonitor: NetworkConnectivityMonitor,
    private val mikrotikReachabilityMonitor: MikrotikReachabilityMonitor
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Result<Flow<Boolean>> = runCatching {
        connectivityMonitor.observeConnectivity().mapLatest { connected ->
            !connected || !mikrotikReachabilityMonitor.isMikrotikReachable()
        }
    }
}
