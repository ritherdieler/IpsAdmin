package com.dscorp.ispadmin.presentation.ui.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Loging
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

/**
 * Created by Sergio Carrillo Diestra on 20/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class LoginViewModel : ViewModel() {

    private val repository: IRepository by inject(IRepository::class.java)
    val loginResponseLiveData = MutableLiveData<LoginResponse>()
    val loginFormErrorLiveData = MutableLiveData<LoginFormError>()

    init {
        viewModelScope.launch {
            val response = repository.getUserSession()
            if (response != null)
                loginResponseLiveData.postValue(LoginResponse.OnLoginSuccess(response))
        }
    }
    fun doLogin(login: Loging) = viewModelScope.launch {

        try {
            if (formIsValid(login)) {
                val responseFromRepository = repository.doLogin(login)
                loginResponseLiveData.postValue(LoginResponse.OnLoginSuccess(responseFromRepository))
            }
        } catch (error: Exception) {
            loginResponseLiveData.postValue(LoginResponse.OnError(error))
        }
    }



    private fun formIsValid(login: Loging): Boolean {

       if (login.username.isEmpty()) {
           loginFormErrorLiveData.value=LoginFormError.OnEtUser("el usuario no puede estar vacio")
           return false
       }

       if (login.password.isEmpty()) {
           loginFormErrorLiveData.value = LoginFormError.OnEtPassword("la contrana no puede estar vacia")
           return false
       }
       return true
   }
}
