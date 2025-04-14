package com.dscorp.ispadmin.presentation.ui.features.payment.history

import MyTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentHistoryComposeFragment : Fragment() {
    private val viewModel: PaymentHistoryViewModel by viewModel()
    private val args by navArgs<PaymentHistoryComposeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscriptionId = args.subscriptionId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {
                    PaymentHistoryScreen(
                        viewModel = viewModel,
                        onPaymentItemClicked = { payment ->
                            val action = if (!payment.paid) 
                                PaymentHistoryComposeFragmentDirections.toRegisterPayment(payment)
                            else 
                                PaymentHistoryComposeFragmentDirections.actionPaymentHistoryFragmentToPaymentDetailFragment(payment)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLastPayments(PaymentHistoryViewModel.LAST_PAYMENTS_ROW_LIMIT)
    }
} 