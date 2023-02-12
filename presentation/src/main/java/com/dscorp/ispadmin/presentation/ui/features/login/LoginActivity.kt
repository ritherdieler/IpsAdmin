package com.dscorp.ispadmin.presentation.ui.features.login

import android.content.Intent
import android.os.Bundle
import android.widget.Checkable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendLoginEvent
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.ui.features.base.BaseActivity
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivity
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterActivity
import com.example.cleanarchitecture.domain.domain.entity.Loging
import com.example.cleanarchitecture.domain.domain.entity.User
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, true)
        setContentView(binding.root)

        observe()

        binding.btLogin.setOnClickListener {
            firebaseAnalytics.sendLoginEvent(AnalyticsConstants.LOGIN)
            doLogin()
        }

        binding.tvCreateAccount.setOnClickListener {
            navigateToRegister()
        }
        binding.cbCheckBox.setOnClickListener {

            if (binding.cbCheckBox.isChecked) {
                val context = this
                val text = "Se ah marcado el check"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            } else {
                val context = this
                val text = "SE QUITO EL CHEEEECK!!!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
        }
    }


    private fun observeLoginFormError() {
        viewModel.loginFormErrorLiveData.observe(this) { formError ->
            when (formError) {
                is LoginFormError.OnEtPassword -> binding.etPassword.error = formError.error
                is LoginFormError.OnEtUser -> binding.etUser.error = formError.error
            }
        }
    }

    private fun observeLoginResponse() {
        viewModel.loginResponseLiveData.observe(this) { response ->
            when (response) {
                is LoginResponse.OnError -> showErrorDialog(response.error.message)
                is LoginResponse.OnLoginSuccess -> navigateToMainActivity(response.user)
            }
        }
    }

    private fun navigateToMainActivity(user: User) {
        FirebaseCrashlytics.getInstance().setUserId(user.id.toString())
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
        val login = Loging(
            binding.etUser.text.toString(),
            binding.etPassword.text.toString(),
            binding.cbCheckBox.isChecked
        )

        viewModel.doLogin(login)
    }

    private fun navigateToRegister() {

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}