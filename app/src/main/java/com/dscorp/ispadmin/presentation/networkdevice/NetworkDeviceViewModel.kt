package com.dscorp.ispadmin.presentation.networkdevice

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.NetworkDevice
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
            println("Debes escribir un nombre para el dispositivo de red")
            etName.setError("El nombre del dispositivo de red no puede estar vacio")
        }
        if (password.isEmpty()) {
            print("debes escribir una contraseña")
            etPassword.setError("La contraseña no puede estar vacia")
        }
        if (username.isEmpty()) {
            println("Debes escribir un usuario")
            etUsername.setError("El usuario no puede estar vacio")
        }
        if (ipaddress.isEmpty()) {
            println("Debes escribir una direccion de ip")
            etIpAddress.setError("La direccion de ip no puede estar vacia")
        }
        val planObject = NetworkDevice(
            name = name,
            Password = password,
            username = username,
            ipAddress = ipaddress)

        viewModelScope.launch {
            try {
                var networkDeviceFromRepository = repository.registerNetworkDevice(planObject)
                networkDeviceResponseLiveData.postValue(NetworkDeviceResponse.OnNetworkDeviceRegistered
                    (networkDeviceFromRepository))
            } catch (error: Exception) {
                networkDeviceResponseLiveData.postValue(NetworkDeviceResponse.OnError(error))

            }

        }


    }
}
