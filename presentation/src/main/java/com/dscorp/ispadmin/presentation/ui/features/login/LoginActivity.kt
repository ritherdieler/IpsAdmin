package com.dscorp.ispadmin.presentation.ui.features.login

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.extension.showCrossDialog
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivity
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterActivity
import com.example.cleanarchitecture.domain.domain.entity.User
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    val viewModel: LoginViewModel by viewModel()

    fun handleState(state: BaseUiState<LoginResponse>) {
        state.loading?.let { showProgressBar(it) }
        state.error?.let { showErrorDialog(it.message ?: "") }
        state.uiState?.let { handleLoginResponse(it) }
    }

    private fun handleLoginResponse(it: LoginResponse) = when (it) {
        is LoginResponse.OnLoginSuccess -> handleLoginResponse(it.user)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(1000)
        val (status, user) = viewModel.checkSessionStatus()
        if (status) {
            handleLoginResponse(user!!)
        } else {
            setContentView(binding.root)

            binding.viewModel = viewModel
            binding.lifecycleOwner = this@LoginActivity
            binding.executePendingBindings()

            binding.tvCreateAccount.setOnClickListener {
                navigateToRegister()
            }

            viewModel.uiState.observe(this) {
                handleState(it)
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

    private fun showProgressBar(progressBar: Boolean) {
        binding.pbLoading.visibility = if (progressBar) ProgressBar.VISIBLE else ProgressBar.GONE
    }

    private fun handleLoginResponse(user: User) {
        if (!user.verified)
            showCrossDialog(R.string.your_account_isnt_verified, lottieRes = R.raw.info)
        else {
            FirebaseCrashlytics.getInstance().setUserId(user.id.toString())
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
