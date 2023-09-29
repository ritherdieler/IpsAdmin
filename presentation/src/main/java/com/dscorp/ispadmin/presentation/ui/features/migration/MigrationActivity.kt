package com.dscorp.ispadmin.presentation.ui.features.migration

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.databinding.ActivityMigrationActivityBinding
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MigrationActivity : AppCompatActivity() {

    private val viewModel: MigrationViewModel by viewModel()

    private lateinit var binding: ActivityMigrationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMigrationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getMigrationFormData()

        val subscriptionResponse =
            intent.getSerializableExtra("subscriptionResponse") as SubscriptionResponse


        lifecycleScope.launch {
            viewModel.uiState.collect {
                when (it) {
                    MigrationUiState.Empty -> {}
                    is MigrationUiState.Error -> showErrorDialog(it)
                    is MigrationUiState.FormDataReady -> showMigrationForm(it, subscriptionResponse)
                    MigrationUiState.Loading -> showLoader()
                    is MigrationUiState.Success -> {
                        Toast.makeText(this@MigrationActivity, "Migración exitosa", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        }

    }

    private fun showLoader() {
        binding.migrationComposeView.setContent {
            Loader()
        }
    }

    private fun showMigrationForm(
        it: MigrationUiState.FormDataReady,
        subscriptionResponse: SubscriptionResponse
    ) {
        binding.migrationComposeView.setContent {
            MigrationForm(
                onus = it.unconfirmedOnus,
                plans = it.plans,
                onMigrationRequest = { request ->
                    request.apply { subscriptionId = subscriptionResponse.id }
                    viewModel.doMigration(request)
                }
            )
        }
    }

    private fun showErrorDialog(it: MigrationUiState.Error) {
        binding.migrationComposeView.setContent {
            ErrorDialog(
                error = it.error.message ?: "",
                onDismissRequest = {
                    viewModel.getMigrationFormData()
                }
            )
        }
    }

}