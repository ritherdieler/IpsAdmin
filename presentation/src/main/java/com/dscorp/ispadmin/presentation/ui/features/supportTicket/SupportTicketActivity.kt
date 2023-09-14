package com.dscorp.ispadmin.presentation.ui.features.supportTicket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.dscorp.ispadmin.databinding.ActivityAssistanceTicketBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


const val TICKET_ID:String = "mticketId"

class TicketActivity : BaseActivity<SupportTicketState, ActivityAssistanceTicketBinding>() {

    override val viewModel: SupportTicketViewModel by viewModel()
    override val binding by lazy { ActivityAssistanceTicketBinding.inflate(layoutInflater) }

    override fun handleState(state: SupportTicketState) {
        when (state) {
            is SupportTicketState.Empty -> {}
            is SupportTicketState.Success -> {
                binding.ticket = state.ticket
                binding.executePendingBindings()
            }

            is SupportTicketState.TicketList -> {}
            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val ticketId = intent.extras?.getString(TICKET_ID)
        ticketId?.let {
            viewModel.getTicket(ticketId)
        }



    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {

        viewModel.uiState.observe(this) { uiState ->
            uiState.error?.let { error -> showErrorDialog(error.message ?: "") }
            uiState.loading?.let { isLoading -> onLoading(isLoading) }
            uiState.uiState?.let { handleState(it) }
        }

        val ticketId = intent.extras?.getString(TICKET_ID)
        ticketId?.let {
            viewModel.getTicket(ticketId)
        }

        super.onNewIntent(intent)
    }

}