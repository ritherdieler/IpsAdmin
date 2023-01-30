package com.dscorp.ispadmin.presentation.ui.features.networkdevice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.networkdevice.NetworkDeviceFormError.*
import com.dscorp.ispadmin.presentation.ui.features.networkdevice.NetworkDeviceResponse.*
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NetworkDeviceViewModel() : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val networkDeviceResponseLiveData = MutableLiveData<NetworkDeviceResponse>()
    val networkDeviceFormErrorLiveData = MutableLiveData<NetworkDeviceFormError>()

    init {
        getFormData()
    }

    private fun getFormData() = viewModelScope.launch {
        try {
            val networkDeviceTypes = repository.getNetworkDeviceTypes()
            networkDeviceResponseLiveData.postValue(OnNetworkDeviceTypesReceived(networkDeviceTypes))
        } catch (error: Exception) {
            networkDeviceResponseLiveData.postValue(OnError(error))
        }
    }

    fun registerNetworkDevice(networkDevice: NetworkDevice) = viewModelScope.launch {
        try {
            if (!formIsValid(networkDevice)) return@launch
            val networkDeviceFromRepository = repository.registerNetworkDevice(networkDevice)
            networkDeviceResponseLiveData.postValue(
                OnNetworkDeviceRegistered(
                    networkDeviceFromRepository
                )
            )

        } catch (error: Exception) {
            networkDeviceResponseLiveData.postValue(OnError(error))
        }
    }

    private fun formIsValid(networkDevice: NetworkDevice): Boolean {

        if (networkDevice.name.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(OnEtNameError())
            return false
        }
        if (networkDevice.username.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(OnEtUserNameError())
            return false
        }
        if (networkDevice.password.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(OnEtPasswordError())
            return false
        }
        if (networkDevice.ipAddress.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(OnEtAddressError())
            return false
        }
        if (networkDevice.networkDeviceType.isNullOrEmpty()) {
            networkDeviceFormErrorLiveData.postValue(OnDeviceTypeError())
            return false
        }
        return true
    }
}
