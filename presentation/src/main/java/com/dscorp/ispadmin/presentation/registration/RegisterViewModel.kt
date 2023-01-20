package com.dscorp.ispadmin.presentation.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.User
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

/**
 * Created by Sergio Carrillo Diestra on 18/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class RegisterViewModel : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)
    val registerResponseLiveData = MutableLiveData<RegisterResponse>()
    val registerFormErrorLiveData = MutableLiveData<RegisterFormError>()
    fun validateForm(
        user: String,
        password1: String,
        password2: String,
        firstName: String,
        lastName: String
    ) {

        if (user.isEmpty()) {
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtUserError("Debes escribir un usuario"))
            return
        }

        if (firstName.isEmpty()) {
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtFirstNameError("debes escribir un nombre"))
            return
        }

        if (lastName.isEmpty()) {
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtLastNameError("debes escribir un apellido"))
            return
        }
        if (password1.isEmpty()) {
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtPassword1Error("debes escribir una contrasena"))
            return
        }

        if (password2.isEmpty()) {
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtPassword2Error("debes escribir una contrasena"))
            return
        }

        if (password1 != password2) {
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtPassword1Error("las contrasenas no coinciden"))
            registerFormErrorLiveData.postValue(RegisterFormError.OnEtPassword2Error("las contrasenas no coinciden"))
            return

        }
        val planObject = User(null, firstName, lastName, 0, user, password1, false)

        viewModelScope.launch {
            try {
                val registerFormRepository = repository.registerUser(planObject)
                registerResponseLiveData.postValue(
                    RegisterResponse.OnRegister(
                        registerFormRepository
                    )
                )

            } catch (error: Exception) {
                registerResponseLiveData.postValue(RegisterResponse.OnError(error))

            }
        }
    }
}
