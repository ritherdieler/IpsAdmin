package com.dscorp.ispadmin.presentation.ui.features.supportTicket.pendingTIckets

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.databinding.FragmentListTicketsBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketAdapter
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketHelper
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketState
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PendingTicketsFragment : BaseFragment<SupportTicketState, FragmentListTicketsBinding>() {

    override val viewModel: SupportTicketViewModel by viewModel()
    override val binding by lazy { FragmentListTicketsBinding.inflate(layoutInflater) }
    private val adapter by lazy {
        SupportTicketAdapter(
            onTicketButtonClicked = {
                lifecycleScope.launch {
                    try {
                        it.isLoading.value = true
                        viewModel.takeTicket(it.ticket.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        it.isLoading.value = false
                    }
                }
            },
            onCloseTicketButtonClicked = {
                lifecycleScope.launch {
                    try {
                        it.isLoading.value = true
                        viewModel.closeUnattendedTicket(it.ticket)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        it.isLoading.value = false
                    }
                }
            },
            user = viewModel.user,
            lifecycleOwner = this
        )
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
                populateRecyclerView(state.ticketList.map { SupportTicketHelper(MutableLiveData(false), it) })
            }

            is SupportTicketState.UpdatedTicket -> {
                val list = adapter.currentList.filter {
                    it.ticket.id != state.ticket.id
                }
                adapter.submitList(list)
            }

            else -> {}
        }
    }

    private fun populateRecyclerView(tickets: List<SupportTicketHelper>) {
        adapter.submitList(tickets)
    }


}