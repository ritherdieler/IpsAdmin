package com.dscorp.ispadmin.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.napbox.NapBoxFragment
import com.dscorp.ispadmin.presentation.networkdevice.NetworkDeviceFragment
import com.dscorp.ispadmin.presentation.place.PlaceFragment
import com.dscorp.ispadmin.presentation.plan.PlanFragment
import com.dscorp.ispadmin.presentation.serviceorden.ServiceOrderFragment
import com.dscorp.ispadmin.presentation.subscription.SubscriptionFragment
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListFragment
import com.dscorp.ispadmin.presentation.technician.TechnicianFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
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
                R.id.nav_see_subscriptions -> showSubscriptionsListFragments()
                R.id.nav_to_register -> showPlaceFragment()
                R.id.nav_to_register_technician -> showTechnicianFragment()
                R.id.nav_to_register_nap_box -> showRegisterNapBoxFragment()
                R.id.nav_to_register_service_order -> showRegisterServiceOrderFragment()
            }
            drawerLayout.close()
            true
        }
    }

    private fun showRegisterNapBoxFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<NapBoxFragment>(R.id.fragmentContainer)
        }
    }

    private fun showTechnicianFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<TechnicianFragment>(R.id.fragmentContainer)
        }
    }

    private fun showSubscriptionsListFragments() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<SubscriptionsListFragment>(R.id.fragmentContainer)
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

    private fun showPlaceFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<PlaceFragment>(R.id.fragmentContainer)
        }
    }
    private fun showRegisterServiceOrderFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<ServiceOrderFragment>(R.id.fragmentContainer)
        }
    }
}