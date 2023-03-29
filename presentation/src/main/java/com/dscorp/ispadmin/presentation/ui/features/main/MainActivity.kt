package com.dscorp.ispadmin.presentation.ui.features.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityMainBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseActivity
import com.dscorp.ispadmin.presentation.util.FCM_ALL_THEME
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val viewModel: MainActivityViewModel by inject()
    private val PERMISSION_CODE = 123

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic(FCM_ALL_THEME)

        // Crear el canal de notificación si la versión de Android es mayor o igual a Android Oreo
        val channel = NotificationChannel(
            "default",
            "Canal de notificaciones",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        // Verificar si ya se han otorgado permisos para las notificaciones
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos aún no se han otorgado, solicitar permiso
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_CODE)
        } else {

        }
        lifecycleScope.launch {

            viewModel.uiState.collect {
                when (it) {
                    UiState.Idle -> {}
                    is UiState.UserSessionsFound -> firebaseAnalytics.setUserId(it.response.id.toString())
                }
            }
        }


        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navView.setupWithNavController(navController)
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
