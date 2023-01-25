package com.dscorp.ispadmin.presentation.subscriptionfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentFindSubscriptionBinding
import org.koin.android.ext.android.inject

class FindSubscriptionFragment : Fragment() {

    private val viewModel: FindSubscriptionViewModel by inject()

    private val binding: FragmentFindSubscriptionBinding by lazy {
        FragmentFindSubscriptionBinding.inflate(layoutInflater)
    }

    private val adapter = FindSubscriptionAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.findSubscriptionRecyclerView.adapter = adapter
        binding.btnFind.setOnClickListener {
            val dni = binding.findSubscriptionEditText.text.toString()
            viewModel.findSubscription(dni)
        }

        observeUiState()

        return binding.root
    }

    private fun observeUiState() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is FindSubscriptionUiState.OnSubscriptionFound -> adapter.submitList(it.subscriptions)
                is FindSubscriptionUiState.OnError -> TODO()
            }
        }
    }

}