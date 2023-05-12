package com.dscorp.ispadmin.presentation.ui.features.login

import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.extension.encryptWithSHA384
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.cleanarchitecture.domain.domain.entity.Loging
import com.example.cleanarchitecture.domain.domain.entity.User
import com.example.data2.data.repository.IRepository

class LoginViewModel(private val repository: IRepository) : BaseViewModel<LoginResponse>() {


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

    fun checkSessionStatus(): Pair<Boolean, User?> {
        val status = repository.getRememberSessionCheckBoxStatus()
        if (status) {
            repository.getUserSession()?.let {
                uiState.value = BaseUiState(LoginResponse.OnLoginSuccess(it))
                return Pair(true, it)
            }
            return Pair(false, null)
        }

        return Pair(false, null)
    }

    fun doLogin() = executeWithProgress {
        if (!formIsValid()) return@executeWithProgress
        val login =
            Loging(username.value!!, password.value!!, remember.value)
        val responseFromRepository = repository.doLogin(login)
        uiState.value = BaseUiState(LoginResponse.OnLoginSuccess(responseFromRepository))
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
