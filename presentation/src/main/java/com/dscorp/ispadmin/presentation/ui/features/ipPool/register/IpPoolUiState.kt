package com.dscorp.ispadmin.presentation.ui.features.ipPool.register

import com.example.cleanarchitecture.domain.domain.entity.IpPool

sealed class IpPoolUiState(val error: String? = null) {
    companion object {
        const val EMPTY_POOL_ERROR = "Este campo no puede estar vacio"
        const val IP_POOL_INVALID = "Esta ip no es valida"
    }

    class IpPoolRegister(val ipPool: IpPool) : IpPoolUiState()
    class IpPoolRegisterError(val registerError: String?) : IpPoolUiState()

    class IpPoolList(val ipPools: List<IpPool>) : IpPoolUiState()
    class IpPoolListError(val message: String?) : IpPoolUiState()

    // FORM STATES
    class IpPoolError : IpPoolUiState(EMPTY_POOL_ERROR)
    class IpPoolInvalidIpSegment : IpPoolUiState(IP_POOL_INVALID)

    object IpPoolCleanError : IpPoolUiState()
    object IpPoolCleanInvalidIpSegment : IpPoolUiState()
}
