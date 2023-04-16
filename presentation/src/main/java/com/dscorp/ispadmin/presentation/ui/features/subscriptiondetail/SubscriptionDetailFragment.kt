package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

class SubscriptionDetailFragment : BaseFragment() {
    private val args: SubscriptionDetailFragmentArgs by navArgs()
    val binding by lazy { FragmentSubscriptionDetailBinding.inflate(layoutInflater) }
    lateinit var subscription: SubscriptionResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscription = args.subscription
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding.subscription = subscription
        binding.executePendingBindings()

       binding.etLocation.setOnClickListener {
           findNavController().navigate(
             SubscriptionDetailFragmentDirections.actionSubscriptionDetailToMapView(
                 subscription.location
                    )
            )
       }

        return binding.root
    }
}
