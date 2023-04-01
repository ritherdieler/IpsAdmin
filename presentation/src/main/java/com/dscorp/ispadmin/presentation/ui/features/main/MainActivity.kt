package com.dscorp.ispadmin.presentation.ui.features.main

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityMainBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseActivity
import com.dscorp.ispadmin.presentation.util.FCM_ALL_THEME
import com.dscorp.ispadmin.presentation.util.FCM_CUSTOMER_THEME
import com.dscorp.ispadmin.presentation.util.FCM_TECHNICIAN_THEME
import com.example.cleanarchitecture.domain.domain.entity.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val viewModel: MainActivityViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navView.setupWithNavController(navController)
        lifecycleScope.launch {

            viewModel.uiState.collect {
                when (it) {
                    UiState.Idle -> {}
                    is UiState.UserSessionsFound -> {
                        firebaseAnalytics.setUserId(it.response.id.toString())
                        when (it.response.type) {
                            User.UserType.TECHNICIAN -> {
                                navView.menu.findItem(R.id.nav_dashboard).isVisible = false
                                navView.menu.findItem(R.id.nav_reports).isVisible = false
                                navView.menu.findItem(R.id.nav_ip_pool).isVisible = false
                                FirebaseMessaging.getInstance()
                                    .subscribeToTopic(FCM_TECHNICIAN_THEME)
                            }
                            User.UserType.SECRETARY -> {
                                navView.menu.findItem(R.id.nav_ip_pool).isVisible = false
                                navView.menu.findItem(R.id.nav_dashboard).isVisible = false

                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(navController.graph.startDestinationId, true)
                                    .build()
                                navController.navigate(
                                    R.id.nav_find_subscriptions,
                                    null,
                                    navOptions
                                )
                            }
                            User.UserType.CLIENT -> {
                                FirebaseMessaging.getInstance().subscribeToTopic(FCM_CUSTOMER_THEME)
                            }
                            User.UserType.LOGISTIC -> {}
                            User.UserType.SALES -> {}
                            User.UserType.ADMIN -> {
                                FirebaseMessaging.getInstance().subscribeToTopic(FCM_ALL_THEME)
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_dashboard) {
            // Si estamos en el destino principal, mostrar el diálogo aquí
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Salir")
            builder.setMessage("¿Estás seguro que quieres salir?")
            builder.setPositiveButton("Sí") { _, _ ->
                finish()
            }
            builder.setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()
        } else {
            // Si no estamos en el destino principal, continuar con el comportamiento predeterminado
            super.onBackPressed()
        }
    }
}
