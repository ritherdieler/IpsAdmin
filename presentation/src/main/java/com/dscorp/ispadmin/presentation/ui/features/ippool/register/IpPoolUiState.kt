package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import com.example.cleanarchitecture.domain.domain.entity.IpPool
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice

sealed class IpPoolUiState(val error: String? = null) {
    companion object {
        const val EMPTY_POOL_ERROR = "Este campo no puede estar vacio"
        const val IP_POOL_INVALID = "Este Segmento no es valido"
        const val HOST_DEVICE_NOT_SELECTED = "Debes seleccionar un dispositivo"
    }

    class IpPoolRegister(val ipPool: IpPool) : IpPoolUiState()

    class IpPoolList(val ipPools: List<IpPool>) : IpPoolUiState()
    class IpPoolListError(val message: String?) : IpPoolUiState()
    class LoadingData(val loading: Boolean) : IpPoolUiState()

    // FORM STATES
    class IpPoolError : IpPoolUiState(EMPTY_POOL_ERROR)
    class IpPoolInvalidIpSegment : IpPoolUiState(IP_POOL_INVALID)
    class HostDevicesReady(val hostDevices: List<NetworkDevice>) : IpPoolUiState()
    class HostDevicesError(val message: String?) : IpPoolUiState()
    object NoHostDeviceSelectedError : IpPoolUiState(HOST_DEVICE_NOT_SELECTED)

    object IpPoolCleanError : IpPoolUiState()
    object IpPoolCleanInvalidIpSegment : IpPoolUiState()
    object CleanNoHostDeviceSelectedError : IpPoolUiState()
}
