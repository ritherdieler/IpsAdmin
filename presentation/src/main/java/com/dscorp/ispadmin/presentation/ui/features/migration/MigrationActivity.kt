package com.dscorp.ispadmin.presentation.ui.features.migration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscorp.ispadmin.databinding.ActivityMigrationActivityBinding

class MigrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMigrationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMigrationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.migrationComposeView.setContent {

        }


    }

}