package com.dscorp.ispadmin.presentation.ui.features.serviceorder.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dscorp.ispadmin.databinding.ActivityServiceOrderDetailBinding


class ServiceOrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val body = intent?.getStringExtra("body")
        val title = intent?.getStringExtra("title")
        Log.d("TAG", "Body: $body, Title: $title")
        binding.tvSeePriority.text = body
        binding.tvSeeProblemDescription.text = title
    }
}

