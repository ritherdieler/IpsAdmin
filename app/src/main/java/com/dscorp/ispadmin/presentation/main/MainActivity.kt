package com.dscorp.ispadmin.presentation.main

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityMainBinding
import com.dscorp.ispadmin.presentation.napbox.NapBoxFragment
import com.dscorp.ispadmin.presentation.napboxeslist.NapBoxesListFragment
import com.dscorp.ispadmin.presentation.networkdevice.NetworkDeviceFragment
import com.dscorp.ispadmin.presentation.place.PlaceFragment
import com.dscorp.ispadmin.presentation.plan.PlanFragment
import com.dscorp.ispadmin.presentation.serviceorden.ServiceOrderFragment
import com.dscorp.ispadmin.presentation.serviceorderlist.ServicesOrderListFragment
import com.dscorp.ispadmin.presentation.subscription.SubscriptionFragment
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListFragment
import com.dscorp.ispadmin.presentation.technician.TechnicianFragment
import com.dscorp.ispadmin.presentation.technicianslist.TechniciansListFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, true)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navView.setupWithNavController(navController)
    }
}