package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.extensions.IsValidIpv4Segment
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IpPoolViewModel : ViewModel(), KoinComponent {

    val uiState: MutableLiveData<IpPoolUiState> = MutableLiveData()
    private val repository: IRepository by inject()

    fun registerIpPool(ipPool: IpPoolRequest) = viewModelScope.launch {
        try {
            if (!formIsValid(ipPool)) return@launch
            val response = repository.registerIpPool(ipPool)
            uiState.value = IpPoolUiState.IpPoolRegister(response)
        } catch (e: Exception) {
            e.printStackTrace()
            uiState.value = IpPoolUiState.IpPoolRegisterError(e.message)
        }
    }

    private fun formIsValid(ipPool: IpPoolRequest): Boolean {

        if(ipPool.hostDeviceId == null){
            uiState.value = IpPoolUiState.NoHostDeviceSelectedError
            return false
        }else{
            uiState.value = IpPoolUiState.CleanNoHostDeviceSelectedError
        }
        if (ipPool.ipSegment == null || ipPool.ipSegment!!.isEmpty()) {
            uiState.value = IpPoolUiState.IpPoolError()
            return false
        } else {
            uiState.value = IpPoolUiState.IpPoolCleanError
        }
        if (!ipPool.ipSegment!!.IsValidIpv4Segment()) {
            uiState.value = IpPoolUiState.IpPoolInvalidIpSegment()
            return false
        } else {
            uiState.value = IpPoolUiState.IpPoolCleanInvalidIpSegment
        }

        return true
    }

    fun getIpPoolList() = viewModelScope.launch {
        try {
            val response = repository.getIpPoolList()
            uiState.value = IpPoolUiState.IpPoolList(response)
        } catch (e: Exception) {
            e.printStackTrace()
            uiState.value = IpPoolUiState.IpPoolListError(e.message)
        }
    }

    fun getHostDevices() = viewModelScope.launch {
        try {
            val response = repository.getHostDevices()
            uiState.postValue(IpPoolUiState.HostDevicesReady(response))
        } catch (e: Exception) {
            uiState.postValue(IpPoolUiState.HostDevicesError(e.message))
        }


    }
}