package com.dscorp.ispadmin.presentation.networkdevice

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NetworkDeviceViewModel() : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val networkDeviceResponseLiveData = MutableLiveData<NetworkDeviceResponse>()
    val networkDeviceFormErrorLiveData = MutableLiveData<NetworkDeviceFormError>()

    fun validateForm(
        etName: EditText,
        etPassword: EditText,
        etUsername: EditText,
        etIpAddress: EditText,
        name: String,
        password: String,
        username: String,
        ipaddress: String,
    ) {

        if (name.isEmpty()) {
            etName.setError("El nombre del dispositivo de red no puede estar vacio")
            return
        }
        if (password.isEmpty()) {
            etPassword.setError("La contraseña no puede estar vacia")
            return
        }
        if (username.isEmpty()) {
            etUsername.setError("El usuario no puede estar vacio")
            return
        }
        if (ipaddress.isEmpty()) {
            etIpAddress.error = "La direccion de ip no puede estar vacia"
            return
        }

        val planObject = NetworkDevice(
            name = name,
            Password = password,
            username = username,
            ipAddress = ipaddress
        )

        viewModelScope.launch {
            try {
                var networkDeviceFromRepository = repository.registerNetworkDevice(planObject)
                networkDeviceResponseLiveData.postValue(
                    NetworkDeviceResponse.OnNetworkDeviceRegistered
                        (networkDeviceFromRepository)
                )
            } catch (error: Exception) {
                networkDeviceResponseLiveData.postValue(NetworkDeviceResponse.OnError(error))

            }

        }


    }
}
