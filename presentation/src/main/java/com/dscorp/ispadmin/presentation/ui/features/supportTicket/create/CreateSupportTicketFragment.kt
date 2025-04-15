package com.dscorp.ispadmin.presentation.ui.features.supportTicket.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.presentation.theme.MyTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateSupportTicketFragment : Fragment() {

    private val viewModel: CreateSupportTicketViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    CreateSupportTicketScreen(
                        uiState = uiState,
                        onPhoneChange = viewModel::updatePhone,
                        onCategoryChange = viewModel::updateCategory,
                        onDescriptionChange = viewModel::updateDescription,
                        onIsClientChange = viewModel::updateIsClient,
                        onPlaceSelected = viewModel::updateSelectedPlace,
                        onSubscriptionSelected = viewModel::updateSelectedSubscription,
                        onSearchTextChange = viewModel::findSubscriptionByNames,
                        onCreateTicket = viewModel::createTicket,
                        onDismissError = viewModel::resetError,
                        categories = viewModel.categories
                    )

                    // Handle navigation on ticket creation
                    if (uiState.isTicketCreated) {
                        LaunchedEffect(Unit) {
                            viewModel.resetTicketCreated()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }
} 