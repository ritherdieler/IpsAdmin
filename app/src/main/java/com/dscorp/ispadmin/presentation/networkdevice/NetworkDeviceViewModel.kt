package com.dscorp.ispadmin.presentation.networkdevice

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.NetworkDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NetworkDeviceViewModel @Inject constructor(private val repository: Repository): ViewModel() {
    val networkDeviceLiveData:MutableLiveData<NetworkDevice> = MutableLiveData ()
    val errorLiveData:MutableLiveData<Exception> = MutableLiveData()

    fun validateForm(
        etName: EditText,
        etPassword: EditText,
        etUsername: EditText,
        etIpAddress: EditText,
        name: String,
        password: String,
        username: String,
        ipaddress: String
    ) {

        if (name.isEmpty()){
            println("Debes escribir un nombre para el dispositivo de red")
            etName.setError("El nombre del dispositivo de red no puede estar vacio")
        }
        if (password.isEmpty()){
            print("debes escribir una contraseña")
            etPassword.setError("La contraseña no puede estar vacia")
        }
        if (username.isEmpty()){
            println("Debes escribir un usuario")
            etUsername.setError("El usuario no puede estar vacio")
        }
        if (ipaddress.isEmpty()){
            println("Debes escribir una direccion de ip")
            etIpAddress.setError("La direccion de ip no puede estar vacia")
        }
        val planObject = NetworkDevice(
            name = name,
            Password =password,
            username = username,
            ipAddress = ipaddress)

        viewModelScope.launch {
            try {

                var response = repository.registerNetworkDevice(planObject)
                networkDeviceLiveData.postValue(response)

            } catch (error: Exception) {
                errorLiveData.postValue(error)

            }

        }



        }
    }