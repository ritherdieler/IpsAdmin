package com.dscorp.ispadmin.presentation.subscriptionlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionsListBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionsListFragment : Fragment() {
    lateinit var binding: FragmentSubscriptionsListBinding
    val viewModel: SubscriptionsListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_subscriptions_list, null, true)
        observe()


        return binding.root
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.responseLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is SubscriptionsListResponse.OnError -> {}
                    is SubscriptionsListResponse.OnSubscriptionFound -> fillRecycleView(it)

                }
            }
        }
    }

    private fun fillRecycleView(it: SubscriptionsListResponse.OnSubscriptionFound) {
        val adapter = SubscriptionAdapter()
        adapter.submitList(it.subscriptions)
        binding.rvSubscription.adapter = adapter

        binding.rvSubscription.visibility = if(it.subscriptions.isNotEmpty())View.VISIBLE else View.GONE
    }
}

