package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentEditSubscriptionBinding
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel

class EditSubscriptionFragment : Fragment() {
    private val args by navArgs<EditSubscriptionFragmentArgs>()
    private val binding by lazy { FragmentEditSubscriptionBinding.inflate(layoutInflater) }
    private val viewModel: SubscriptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.subscription = args.subscription

        return binding.root
    }


}