package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import com.example.cleanarchitecture.domain.entity.IpPool
import com.example.cleanarchitecture.domain.entity.NetworkDevice

sealed class IpPoolUiState(val error: String? = null) {

    class IpPoolRegister(val ipPool: IpPool) : IpPoolUiState()
    class FormDataReady(val hostDevices: List<NetworkDevice>,val ipPoolList: List<IpPool>) :
        IpPoolUiState()
}
