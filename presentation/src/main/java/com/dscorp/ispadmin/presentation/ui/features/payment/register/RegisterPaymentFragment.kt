package com.dscorp.ispadmin.presentation.ui.features.payment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.presentation.theme.MyTheme

class RegisterPaymentFragment : Fragment() {
    private val args: RegisterPaymentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {
                    RegisterPaymentScreen(
                        payment = args.payment,
                        onNavigateBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
} 