package com.dscorp.ispadmin.presentation.ui.features.supportTicket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dscorp.ispadmin.databinding.FragmentSupportTicketListBinding
import com.dscorp.ispadmin.databinding.ItemSupportTicketBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus
import org.koin.androidx.viewmodel.ext.android.viewModel

class SupportTicketListFragment :
    BaseFragment<SupportTicketState, FragmentSupportTicketListBinding>() {

    override val binding by lazy { FragmentSupportTicketListBinding.inflate(layoutInflater) }

    override val viewModel: SupportTicketViewModel by viewModel()

    private val adapter by lazy {
        SupportTicketAdapter(onTicketButtonClicked = {
                viewModel.takeTicket(it.id)
        }, onCloseTicketButtonClicked = {
            viewModel.closeTicket(it.id)
        })
    }

    override fun handleState(state: SupportTicketState) {

        when (state) {
            SupportTicketState.Empty -> {}
            is SupportTicketState.Success -> {}
            is SupportTicketState.TicketList -> {
                adapter.submitList(state.ticketList)
            }

            is SupportTicketState.TicketTaken -> {
                val list =adapter.currentList.map {
                    if (it.id == state.ticket.id) {
                        state.ticket
                    } else {
                        it
                    }
                }
                adapter.submitList(list)
            }

            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerViewSupportTicket.adapter = adapter
    }

}

class SupportTicketAdapter(
    private val onTicketButtonClicked: (AssistanceTicketResponse) -> Unit = {},
    private val onCloseTicketButtonClicked: (AssistanceTicketResponse) -> Unit = {}
    ) :
    ListAdapter<AssistanceTicketResponse, SupportTicketAdapter.SupportTicketViewHolder>(
        SupportTicketDiffUtil()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportTicketViewHolder {
        val binding by lazy { ItemSupportTicketBinding.inflate(LayoutInflater.from(parent.context)) }
        return SupportTicketViewHolder(binding, onTicketButtonClicked,onCloseTicketButtonClicked)

    }

    override fun onBindViewHolder(holder: SupportTicketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SupportTicketViewHolder(
        val binding: ItemSupportTicketBinding,
        val onTicketButtonClicked: (AssistanceTicketResponse) -> Unit,
        val onCloseTicketButtonClicked: (AssistanceTicketResponse) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subscription: AssistanceTicketResponse) {
            binding.ticket = subscription
            binding.buttonVisibility = subscription.getButtonVisibilityByStatus()
            binding.buttonTakeTicket.setOnClickListener {
                onTicketButtonClicked(subscription)
            }

            binding.buttonCloseTicket.setOnClickListener {
                onCloseTicketButtonClicked(subscription)
            }

            binding.executePendingBindings()

        }

        private fun AssistanceTicketResponse.getButtonVisibilityByStatus(): Int {
            return when (status) {
                AssistanceTicketStatus.PENDING -> View.VISIBLE
                AssistanceTicketStatus.ASSIGNED, AssistanceTicketStatus.IN_PROGRESS, AssistanceTicketStatus.RESOLVED, AssistanceTicketStatus.CLOSED -> View.GONE
            }
        }
    }

    class SupportTicketDiffUtil : DiffUtil.ItemCallback<AssistanceTicketResponse>() {
        override fun areItemsTheSame(
            oldItem: AssistanceTicketResponse,
            newItem: AssistanceTicketResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AssistanceTicketResponse,
            newItem: AssistanceTicketResponse
        ): Boolean {
            return oldItem==newItem
        }

    }


}


