package com.dscorp.ispadmin.presentation.ui.features.login

import MyTheme
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.extension.showCrossDialog
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.login.compose.Login
import com.dscorp.ispadmin.presentation.ui.features.login.compose.LoginScreen
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivity
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterActivity
import com.example.cleanarchitecture.domain.domain.entity.User
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(1000)

        binding.composeView.setContent {
            MyTheme {
                LoginScreen(
                    onCreatedAccountClicked = ::navigateToRegister,
                    onLoginSuccess = ::handleLoginResponse,
                    onAcceptUpdate = {
                        finish()
                    },
                    onNoUpdate = {
                        val (status, user) = viewModel.checkSessionStatus()
                        if (status)
                            handleLoginResponse(user!!)
                    }
                )
            }
        }

        setContentView(binding.root)
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
