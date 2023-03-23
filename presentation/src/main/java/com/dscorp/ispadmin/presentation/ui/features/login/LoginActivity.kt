package com.dscorp.ispadmin.presentation.ui.features.login

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseActivity
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivity
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterActivity
import com.example.cleanarchitecture.domain.domain.entity.User
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val (status, user) = viewModel.checkSessionStatus()
        if (status) {
            navigateToMainActivity(user!!)
        } else {
            setContentView(binding.root)

            binding.viewModel = viewModel
            binding.lifecycleOwner = this@LoginActivity
            binding.executePendingBindings()

            observe()

            binding.tvCreateAccount.setOnClickListener {
                navigateToRegister()
            }

            binding.cbCheckBox.setOnClickListener {
            }
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Quieres salir de la aplicación?")
        builder.setMessage("Presiona el botón Aceptar para salir o Cancelar para quedarte en la pantalla actual.")
        builder.setPositiveButton("Aceptar") { _, _ ->
            // Cerrar todas las actividades abiertas y salir de la aplicación
            finishAffinity()
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog = builder.create()
        dialog.show()
    }


    private fun observeLoginFormError() {
        viewModel.loginFormErrorLiveData.observe(this) { formError ->
            when (formError) {
                is LoginFormError.OnEtPassword -> binding.tlPassword.error = formError.error
                is LoginFormError.OnEtUser -> binding.tlUser.error = formError.error
            }
        }
    }

    private fun observeLoginResponse() {
        viewModel.loginResponseLiveData.observe(this) { response ->
            when (response) {
                is LoginResponse.OnError -> showErrorDialog(response.error.message)

                is LoginResponse.OnLoginSuccess -> navigateToMainActivity(response.user)

                is LoginResponse.ShowProgressBarState -> showProgressBar(response.dialogProgress)
            }
        }
    }

    private fun showProgressBar(progressBar: Boolean) {
        binding.pbLoading.visibility = if (progressBar) ProgressBar.VISIBLE else ProgressBar.GONE

    }

    private fun navigateToMainActivity(user: User) {
        FirebaseCrashlytics.getInstance().setUserId(user.id.toString())
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
