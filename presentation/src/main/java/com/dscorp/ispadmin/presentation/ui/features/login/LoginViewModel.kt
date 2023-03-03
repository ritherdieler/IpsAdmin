package com.dscorp.ispadmin.presentation.ui.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.Loging
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

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
            if (!formIsValid(login)) return@launch
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(true)

            val responseFromRepository = repository.doLogin(login)
            delay(2000)
            loginResponseLiveData.value = LoginResponse.OnLoginSuccess(responseFromRepository)
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(false)

        } catch (error: Exception) {
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(true)
            delay(1000)
            loginResponseLiveData.value = LoginResponse.OnError(error)
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(false)

        }
    }

    private fun formIsValid(login: Loging): Boolean {
        if (login.username.isEmpty()) {
            loginFormErrorLiveData.value =
                LoginFormError.OnEtUser("El usuario no puede estar vacío")
            return false
        }
        if (login.password.isEmpty()) {
            loginFormErrorLiveData.value =
                LoginFormError.OnEtPassword("La contraseña no puede estar vacía")
            return false
        }
        return true
    }
}
