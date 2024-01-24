package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentSubscriptionFinderBinding
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionFinderScreen

class SubscriptionFinderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSubscriptionFinderBinding.inflate(layoutInflater)

        val navController = findNavController()

        binding.root.setContent {
            SubscriptionFinderScreen(navController)
        }
        return binding.root
    }

}
