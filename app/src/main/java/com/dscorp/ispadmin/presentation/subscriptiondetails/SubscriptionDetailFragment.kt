package com.dscorp.ispadmin.presentation.subscriptiondetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.domain.entity.Subscription

class SubscriptionDetailFragment : Fragment() {
    private val args: SubscriptionDetailFragmentArgs by navArgs()
    lateinit var binding: FragmentSubscriptionDetailBinding
    lateinit var subscription: Subscription

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
        binding.tvAverSiEstaLlegandoXd.text = subscription.lastName
        binding.tvAverSiEstaLlegandoXd.text = subscription.firstName
        return binding.root
    }
}
