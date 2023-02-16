package com.dscorp.ispadmin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentPaymentDetailBinding

class PaymentDetailFragment : Fragment() {

    private val args: PaymentDetailFragmentArgs by navArgs()
    private val binding by lazy { FragmentPaymentDetailBinding.inflate(layoutInflater) }
    private val viewModel: PaymentDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.payment = args.payment

        return binding.root
    }


}