package com.dscorp.ispadmin.presentation.networkdevice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NetworkDeviceViewModel() : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val networkDeviceResponseLiveData = MutableLiveData<NetworkDeviceResponse>()
    val networkDeviceFormErrorLiveData = MutableLiveData<NetworkDeviceFormError>()

    fun registerNetworkDevice(networkDevice: NetworkDevice) = viewModelScope.launch {
        try {
            if (formIsValid(networkDevice)) {

                var networkDeviceFromRepository = repository.registerNetworkDevice(networkDevice)
                networkDeviceResponseLiveData.postValue(NetworkDeviceResponse.OnNetworkDeviceRegistered
                    (networkDeviceFromRepository))
            }
        } catch (error: Exception) {
            networkDeviceResponseLiveData.postValue(NetworkDeviceResponse.OnError(error))
        }
    }

    private fun formIsValid(networkDevice: NetworkDevice): Boolean {

        if (networkDevice.name.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(NetworkDeviceFormError.OnEtNameError("El nombre del dispositivo de red no puede estar vacio"))
            return false
        }
        if (networkDevice.password.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(NetworkDeviceFormError.OnEtPassword("La contraseña no puede estar vacia"))
            return false
        }
        if (networkDevice.username.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(NetworkDeviceFormError.OnEtUserName("El usuario no puede estar vacio"))
            return false
        }
        if (networkDevice.ipAddress.isEmpty()) {
            networkDeviceFormErrorLiveData.postValue(NetworkDeviceFormError.OnEtAddress("La direccion de ip no puede estar vacia"))
            return false

        }
        return true
    }
}
