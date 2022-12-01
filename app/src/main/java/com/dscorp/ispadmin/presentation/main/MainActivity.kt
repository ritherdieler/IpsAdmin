package com.dscorp.ispadmin.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.Subscription.SubscriptionFragment
import com.dscorp.ispadmin.presentation.networkdevice.NetworkDeviceFragment
import com.dscorp.ispadmin.presentation.plan.PlanFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        showFirstFragment()
        setNavigationClickListener()

    }

    private fun setNavigationClickListener() {
        navigationView.setNavigationItemSelectedListener { elemento ->
            when (elemento.itemId) {
                R.id.nav_plan -> showPlanFragment()
                R.id.nav_device -> showNetworkDeviceFragment()
                R.id.nav_subscription -> showSubscriptionFragment()
            }
            drawerLayout.close()
            true
        }
    }

    private fun showFirstFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<PlanFragment>(R.id.fragmentContainer)
        }
    }

    private fun showNetworkDeviceFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<NetworkDeviceFragment>(R.id.fragmentContainer)
        }
    }

    private fun showPlanFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<PlanFragment>(R.id.fragmentContainer)
        }
    }

    private fun showSubscriptionFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<SubscriptionFragment>(R.id.fragmentContainer)
        }
    }
}