package com.dscorp.ispadmin.presentation.ui.features.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityMainBinding
import com.dscorp.ispadmin.presentation.fcm.FcmTopics
import com.dscorp.ispadmin.presentation.util.FCM_ALL_THEME
import com.dscorp.ispadmin.presentation.util.FCM_CUSTOMER_THEME
import com.dscorp.ispadmin.presentation.util.FCM_TECHNICIAN_THEME
import com.example.cleanarchitecture.domain.domain.entity.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val firebaseAnalytics: FirebaseAnalytics by inject()

    private val viewModel: MainActivityViewModel by inject()

    companion object {
        const val PERMISSION_CODE = 123
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        manageProfilePrivileges(navView, navController)

        navView.setupWithNavController(navController)


        // Verificar si ya se han otorgado permisos para las notificaciones
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si los permisos aún no se han otorgado, solicitar permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_CODE
            )
        }
    }

    private fun manageProfilePrivileges(
        navView: NavigationView,
        navController: NavController
    ) {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navInflater = navHostFragment.navController.navInflater
        val graph = navInflater.inflate(R.navigation.mobile_navigation)
        navHostFragment.navController.graph = graph

        val user = viewModel.user
        user?.let {
            firebaseAnalytics.setUserId(it.id.toString())
            when (user.type) {
                User.UserType.TECHNICIAN -> {
                    navView.menu.findItem(R.id.nav_dashboard).isVisible = false
                    navView.menu.findItem(R.id.nav_reports).isVisible = false
                    navView.menu.findItem(R.id.nav_see_plan_list).isVisible = false
                    navView.menu.findItem(R.id.nav_reports).isVisible = false
                    FirebaseMessaging.getInstance()
                        .subscribeToTopic(FCM_TECHNICIAN_THEME)

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestinationId, true)
                        .build()
                    navController.navigate(
                        R.id.nav_register_subscription,
                        null,
                        navOptions
                    )
                    navHostFragment.navController.navigate(R.id.nav_register_subscription)

                    FirebaseMessaging.getInstance().subscribeToTopic(FcmTopics.ASSISTANCE_TICKET)
                }

                User.UserType.SECRETARY -> {
                    navView.menu.findItem(R.id.nav_dashboard).isVisible = true
                    navView.menu.findItem(R.id.nav_see_plan_list).isVisible = false
                    navView.menu.findItem(R.id.nav_mufa).isVisible = false
                    navView.menu.findItem(R.id.nav_outlays).isVisible = true

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestinationId, true)
                        .build()
                    navController.navigate(
                        R.id.nav_find_subscriptions,
                        null,
                        navOptions
                    )
                    navHostFragment.navController.navigate(R.id.nav_find_subscriptions)
                    FirebaseMessaging.getInstance()
                        .subscribeToTopic(FcmTopics.ASSISTANCE_TICKET_ADMINS)
                    FirebaseMessaging.getInstance().subscribeToTopic(FcmTopics.ASSISTANCE_TICKET)

                }

                User.UserType.CLIENT -> {
//                    FirebaseMessaging.getInstance().subscribeToTopic(FCM_CUSTOMER_THEME)
                }

                User.UserType.ADMIN -> {
//                    FirebaseMessaging.getInstance().subscribeToTopic(FCM_ALL_THEME)
                    navHostFragment.navController.navigate(R.id.nav_dashboard)
                    FirebaseMessaging.getInstance()
                        .subscribeToTopic(FcmTopics.ASSISTANCE_TICKET_ADMINS)
                    FirebaseMessaging.getInstance().subscribeToTopic(FcmTopics.ASSISTANCE_TICKET)
                    navView.menu.findItem(R.id.nav_outlays).isVisible = true

                }

                else -> {
                    navHostFragment.navController.navigate(R.id.nav_find_subscriptions)
                }
            }

            manageSupportTicketVisibility(user, navView)
        }
    }

    private fun manageSupportTicketVisibility(
        user: User,
        navView: NavigationView
    ) {
        when (user.type) {
            User.UserType.ADMIN,
            User.UserType.SECRETARY -> {
                navView.menu.findItem(R.id.nav_create_support_ticket).isVisible = true
                navView.menu.findItem(R.id.nav_support_assistance_tickets).isVisible = true
            }

            else -> {}
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
