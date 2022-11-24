package com.dscorp.ispadmin.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.plan.PlanFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<PlanFragment>(R.id.fragmentContainer)
        }

    }
}