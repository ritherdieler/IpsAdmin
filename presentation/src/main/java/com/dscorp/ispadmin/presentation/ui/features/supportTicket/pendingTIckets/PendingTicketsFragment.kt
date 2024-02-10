package com.dscorp.ispadmin.presentation.ui.features.supportTicket.pendingTIckets

import android.os.Bundle
import com.dscorp.ispadmin.databinding.FragmentListTicketsBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketAdapter
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketState
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketViewModel
import com.example.data2.data.response.AssistanceTicketResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class PendingTicketsFragment : BaseFragment<SupportTicketState, FragmentListTicketsBinding>() {

    override val viewModel: SupportTicketViewModel by viewModel()
    override val binding by lazy { FragmentListTicketsBinding.inflate(layoutInflater) }
    private val adapter by lazy {
        SupportTicketAdapter(onTicketButtonClicked = {
            viewModel.takeTicket(it.id)
        }, onCloseTicketButtonClicked = {
            viewModel.closeTicket(it)
        })
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        binding.adapter = adapter
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPendingTickets()
    }

    override fun handleState(state: SupportTicketState) {

        when (state) {
            is SupportTicketState.TicketList -> {
                populateRecyclerView(state.ticketList)
            }

            is SupportTicketState.UpdatedTicket -> {
                val list = adapter.currentList.filter {
                    it.id != state.ticket.id
                }
                adapter.submitList(list)
            }

            else -> {}
        }
    }

    private fun populateRecyclerView(tickets: List<AssistanceTicketResponse>) {
        adapter.submitList(tickets)
    }


}