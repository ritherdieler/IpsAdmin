package com.dscorp.ispadmin.presentation.ui.features.registration

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityRegisterBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants.REGISTER_USER
import com.dscorp.ispadmin.presentation.extension.analytics.sendSignUpEvent
import com.dscorp.ispadmin.presentation.extension.showCrossDialog
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : BaseActivity() {

    val binding: ActivityRegisterBinding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        observe()
    }

    fun observe() {
        viewModel.uiState.observe(this) {
            it.error?.let { error -> showErrorDialog(error.message ?: "") }
            it.loading?.let { }
            it.uiState?.let { uiState ->
                when (uiState) {
                    is RegisterUiState.OnRegister -> showCrossDialog(getString(R.string.user_register_success)) { finish() }
                }
            }
        }
    }

}
