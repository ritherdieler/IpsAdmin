package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.cleanarchitecture.domain.domain.entity.extensions.IsValidIpv4Segment
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IpPoolViewModel : BaseViewModel<IpPoolUiState>(), KoinComponent {

    private val repository: IRepository by inject()

    fun registerIpPool(ipPool: IpPoolRequest) =executeWithProgress{
            if (!formIsValid(ipPool)) return@executeWithProgress
            val response = repository.registerIpPool(ipPool)
            uiState.value = BaseUiState(IpPoolUiState.IpPoolRegister(response))

    }

    private fun formIsValid(ipPool: IpPoolRequest): Boolean {

        if (ipPool.hostDeviceId == null) {
            uiState.value = BaseUiState(IpPoolUiState.NoHostDeviceSelectedError)
            return false
        } else {
            uiState.value = BaseUiState(IpPoolUiState.CleanNoHostDeviceSelectedError)
        }
        if (ipPool.ipSegment == null || ipPool.ipSegment!!.isEmpty()) {
            uiState.value = BaseUiState(IpPoolUiState.IpPoolError())
            return false
        } else {
            uiState.value = BaseUiState(IpPoolUiState.IpPoolCleanError)
        }
        if (!ipPool.ipSegment!!.IsValidIpv4Segment()) {
            uiState.value = BaseUiState(IpPoolUiState.IpPoolInvalidIpSegment())
            return false
        } else {
            uiState.value = BaseUiState(IpPoolUiState.IpPoolCleanInvalidIpSegment)
        }

        return true
    }

    fun getIpPoolList() = executeWithProgress {
            val response = repository.getIpPoolList()
            delay(500)
            uiState.value = BaseUiState(IpPoolUiState.IpPoolList(response))
    }

    fun getHostDevices() = executeWithProgress{
            val response = repository.getHostDevices()
            uiState.value = BaseUiState((IpPoolUiState.HostDevicesReady(response)))
    }
}
