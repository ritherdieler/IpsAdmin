package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import RegisterSubscriptionFormScreen
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.presentation.theme.MyTheme

class RegisterSubscriptionComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {
                    RegisterSubscriptionFormScreen(onSubscriptionRegisterSuccess = {
                        findNavController().popBackStack()
                    })

                }
            }
        }
    }


}