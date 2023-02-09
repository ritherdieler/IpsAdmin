package com.dscorp.ispadmin.presentation.ui.features.login

import android.widget.EditText
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
            val response = repository.getUserSessions()
            if (response != null)
                loginResponseLiveData.postValue(LoginResponse.OnLoginSucess(response))

        }
    }

    fun validateForm(
        user: String,
        password: String,
        etUser: EditText,
        etPassword: EditText,
    ) {

        if (user.isEmpty()) {
            etUser.setError("el usuario no puede estar vacio")
            return
        }

        if (password.isEmpty()) {
            etPassword.setError("la contrana no puede estar vacia")
            return
        }

        var loginObject = Loging(

            username = user,
            password = password
        )


        viewModelScope.launch {
            try {

                var responseFromRepository = repository.doLogin(loginObject)
                loginResponseLiveData.postValue(LoginResponse.OnLoginSucess(responseFromRepository))

            } catch (error: Exception) {
                loginResponseLiveData.postValue(LoginResponse.OnError(error))
            }
        }
    }
}