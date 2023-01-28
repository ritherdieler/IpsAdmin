package com.dscorp.ispadmin.presentation.ui.features.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivity
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, true)
        setContentView(binding.root)

        observe()

        binding.btLogin.setOnClickListener {
            doLogin()
        }

        binding.tvCreateAccount.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun observeLoginFormError() {
        viewModel.loginFormErrorLiveData.observe(this) { formError ->
            when (formError) {
                is LoginFormError.OnEtPassword -> binding.etPassword.setError(formError.error)
                is LoginFormError.OnEtUser -> binding.etUser.setError(formError.error)
            }
        }
    }

    private fun observeLoginResponse() {
        viewModel.loginResponseLiveData.observe(this) { response ->
            when (response) {
                is LoginResponse.OnError -> showErrorDialog(response.error.message)
                is LoginResponse.OnLoginSucess -> navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun showErrorDialog(message: String?) {
        AlertDialog.Builder(this)
            .setTitle(R.string.login_Error_Tittle)
            .setMessage(message)
            .create()
            .show()
    }

    fun observe() {

        observeLoginResponse()
        observeLoginFormError()
    }

    fun doLogin() {

        var usertext = binding.etUser.text.toString()
        var passwordtext = binding.etPassword.text.toString()

        viewModel.validateForm(
            usertext,
            passwordtext,
            binding.etUser,
            binding.etPassword
        )
    }

    fun navigateToRegister() {

        println("Click")

        var intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}