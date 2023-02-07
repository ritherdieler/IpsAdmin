package com.dscorp.ispadmin.presentation.ui.features.ipPool.register

import com.example.cleanarchitecture.domain.domain.entity.IpPool

sealed class IpPoolUiState {

    class IpPoolCreated(ipPool: IpPool) : IpPoolUiState()
    class IpPoolRegisterError(error: String?) : IpPoolUiState()
    object IpPoolInvalidIpSegment : IpPoolUiState()

}
