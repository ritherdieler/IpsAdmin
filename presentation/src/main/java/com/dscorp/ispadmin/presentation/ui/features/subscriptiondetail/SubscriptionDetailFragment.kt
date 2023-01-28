package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

class SubscriptionDetailFragment : Fragment() {
    private val args: SubscriptionDetailFragmentArgs by navArgs()
    lateinit var binding: FragmentSubscriptionDetailBinding
    lateinit var subscription: SubscriptionResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscription = args.subscription
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater, R.layout.fragment_subscription_detail, null, true
            )
        binding.subscription = subscription
        binding.executePendingBindings()

        binding.ivMap.setOnClickListener {
            findNavController().navigate(
                SubscriptionDetailFragmentDirections.actionSubscriptionDetailToMapView(
                    subscription.location
                )
            )
        }



        return binding.root
    }

}
