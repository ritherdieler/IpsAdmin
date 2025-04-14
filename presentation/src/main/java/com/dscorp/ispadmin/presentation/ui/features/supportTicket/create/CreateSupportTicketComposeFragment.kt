package com.dscorp.ispadmin.presentation.ui.features.supportTicket.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.presentation.theme.MyTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateSupportTicketComposeFragment : Fragment() {
    
    private val viewModel: CreateSupportTicketViewModel by viewModel()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {

                    CreateSupportTicketScreen(
                        viewModel = viewModel,
                        onTicketCreated = {
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
} 