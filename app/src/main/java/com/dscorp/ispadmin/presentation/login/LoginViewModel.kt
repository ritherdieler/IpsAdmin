package com.dscorp.ispadmin.presentation.login

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.domain.repository.IRepository
import com.dscorp.ispadmin.domain.entity.Loging
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

    fun validateForm(
        user: String,
        password: String,
        etUser: EditText,
        etPassword: EditText,
    ) {

        if (user.isEmpty()) {

            println("debes escribir un usuario")
            etUser.setError("el usuario no puede estar vacio")
            return
        }

        if (password.isEmpty()) {

            println("debes escribir una contrasena")
            etPassword.setError("la contrana no puede estar vacia")
            return
        }


        var loginObject = Loging(

            username = user,
            password = password

        )

        viewModelScope.launch {
            try {

                var loginFromRepository = repository.doLogin(loginObject)
                loginResponseLiveData.postValue(LoginResponse.OnLoginSucess(loginFromRepository))

            } catch (error: Exception) {
                loginResponseLiveData.postValue(LoginResponse.OnError(error))

            }


        }


    }
}