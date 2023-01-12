package com.dscorp.ispadmin.presentation.subscriptionlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.repository.model.Subscription

class SubscriptionDetailFragment : Fragment() {
    lateinit var binding: FragmentSubscriptionDetailBinding
    lateinit var subscription: Subscription
    companion object {
        @JvmStatic
        fun newInstance(subscription: Subscription) =
            SubscriptionDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("subscription", subscription)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         subscription = arguments?.getSerializable("subscription") as Subscription
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding=
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_subscription_detail, null, true
        )
    binding.tvAverSiEstaLlegandoXd.text=subscription.lastName
        binding.tvAverSiEstaLlegandoXd.text =subscription.firstName
        return binding.root
    }
}
