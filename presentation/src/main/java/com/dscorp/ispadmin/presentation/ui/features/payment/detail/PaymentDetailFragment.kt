package com.dscorp.ispadmin.presentation.ui.features.payment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentPaymentDetailBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment

class PaymentDetailFragment : BaseFragment() {

    private val args: PaymentDetailFragmentArgs by navArgs()
    private val binding by lazy { FragmentPaymentDetailBinding.inflate(layoutInflater) }
    private val viewModel: PaymentDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.payment = args.payment
        binding.executePendingBindings()

        return binding.root
    }
}
