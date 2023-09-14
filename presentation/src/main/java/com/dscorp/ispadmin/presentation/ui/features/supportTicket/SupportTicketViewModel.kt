package com.dscorp.ispadmin.presentation.ui.features.supportTicket

import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus


class SupportTicketViewModel(
    private val repository: IRepository,
) : BaseViewModel<SupportTicketState>() {

    val user = repository.getUserSession()!!

    init {
        executeWithProgress {
            val result = repository.getTicketsByStatus(AssistanceTicketStatus.PENDING)
            uiState.postValue(BaseUiState(SupportTicketState.TicketList(result)))
        }
    }

    fun getTicket(ticketId: String) = executeWithProgress {
        val response = repository.getTicket(ticketId)
        uiState.postValue(BaseUiState(SupportTicketState.Success(response)))
    }

    fun takeTicket(id: Int) = executeWithProgress {
        val response = repository.updateTicketState(id, AssistanceTicketStatus.ASSIGNED, user.id!!)
        uiState.postValue(BaseUiState(SupportTicketState.TicketTaken(response)))
    }

    fun closeTicket(id: Int) = executeWithProgress {
        val response = repository.updateTicketState(id, AssistanceTicketStatus.CLOSED, user.id!!)
        uiState.postValue(BaseUiState(SupportTicketState.TicketTaken(response)))
    }


}

sealed class SupportTicketState {
    object Empty : SupportTicketState()
    data class TicketTaken(val ticket: AssistanceTicketResponse) : SupportTicketState()

    data class Success(val ticket: AssistanceTicketResponse) : SupportTicketState()

    data class TicketList(val ticketList: List<AssistanceTicketResponse>) : SupportTicketState()
}

