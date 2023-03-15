package com.dscorp.ispadmin.presentation.ui.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.cleanarchitecture.domain.domain.entity.Loging
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class LoginViewModel : ViewModel() {
    private val repository: IRepository by inject(IRepository::class.java)
    val loginResponseLiveData = MutableLiveData<LoginResponse>()
    val loginFormErrorLiveData = MutableLiveData<LoginFormError>()


    val username = FormField(
        hintResourceId = R.string.username,
        errorResourceId = R.string.mustDigiUsername,
        fieldValidator = object : FieldValidator<String> {
            override fun validate(fieldValue: String?): Boolean {
                return !fieldValue.isNullOrEmpty()
            }
        })


    val password = FormField(
        hintResourceId = R.string.password,
        errorResourceId = R.string.mustDigitPassword,
        fieldValidator = object : FieldValidator<String?> {
            override fun validate(fieldValue: String?): Boolean {
                return !fieldValue.isNullOrEmpty()
            }
        })


    val remember = FormField(
        hintResourceId = R.string.password,
        errorResourceId = R.string.mustDigitPassword,
        fieldValidator = object : FieldValidator<Boolean?> {
            override fun validate(fieldValue: Boolean?): Boolean {
                return fieldValue != null
            }
        })


    init {
        viewModelScope.launch {
            val response = repository.getUserSession()
            if (response != null)
                loginResponseLiveData.postValue(LoginResponse.OnLoginSuccess(response))
        }
    }

    fun doLogin() = viewModelScope.launch {
        try {
            if (!formIsValid()) return@launch
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(true)
            val login = Loging(username.value!!, password.value!!, remember.value)
            val responseFromRepository = repository.doLogin(login)
            loginResponseLiveData.value = LoginResponse.OnLoginSuccess(responseFromRepository)
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(false)

        } catch (error: Exception) {
            error.printStackTrace()
            loginResponseLiveData.value = LoginResponse.OnError(error)
            loginResponseLiveData.value = LoginResponse.ShowProgressBarState(false)

        }
    }

    fun onCheckedChanged(checked: Boolean) {
        remember.value = checked
    }

    fun onUsernameTextChanged(username: CharSequence) {
        this.username.value = username.toString()
    }

    fun onPasswordTextChanged(password: CharSequence) {
        this.password.value = password.toString()
    }

    private fun formIsValid(): Boolean {
        val fields = listOf(username, password)

        for (field in fields) {
            field.emitErrorIfExists()
        }

        return fields.all { it.isValid }
    }
}
