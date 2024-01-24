package com.dscorp.ispadmin.presentation.ui.features.supportTicket

import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionFastSearchResponse
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import com.example.data2.data.repository.IRepository
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus


class SupportTicketViewModel(
    private val repository: IRepository,
) : BaseViewModel<SupportTicketState>() {

    val user = repository.getUserSession()!!

    val categories = listOf(
        "Sin Conexión a Internet",
        "Internet Lento",
        "Migración a fibra óptica",
        "Cambio de Domicilio",
        "Cambio de Contraseña",
        "Ruptura de cable última milla",
        "Alineamiento de antena CPE",
        "Instalación de Tv Cable",
        "Añadir Tv Cable a su plan de internet",
        "No tiene señal de Tv Cable",
        "Cambio de Onu",
        "Cambio de Router",
        "Instalación de repetidor",
        "Evaluar Factibilidad de Servicio",
        "Instalación de Internet",
        "Otros",
    )

    fun getTicket(ticketId: String) = executeWithProgress {
        val response = repository.getTicket(ticketId)
        uiState.postValue(BaseUiState(SupportTicketState.Success(response)))
    }

    fun takeTicket(id: Int) = executeWithProgress {
        val response = repository.updateTicketState(id, AssistanceTicketStatus.ASSIGNED, user.id!!)
        uiState.postValue(BaseUiState(SupportTicketState.UpdatedTicket(response)))
    }

    fun closeTicket(id: Int) = executeWithProgress {
        val response = repository.updateTicketState(id, AssistanceTicketStatus.CLOSED, user.id!!)
        uiState.postValue(BaseUiState(SupportTicketState.UpdatedTicket(response)))
    }

    fun getClosedTickets() = executeWithProgress {
        val response = repository.getTicketsByStatus(AssistanceTicketStatus.CLOSED)
        uiState.postValue(BaseUiState(SupportTicketState.TicketList(response)))
    }

    fun getPendingTickets() = executeWithProgress {
        val response = repository.getTicketsByStatus(AssistanceTicketStatus.PENDING)
        uiState.postValue(BaseUiState(SupportTicketState.TicketList(response)))
    }

    fun getTakenTickets() = executeWithProgress {
        val response = repository.getTicketsByStatus(AssistanceTicketStatus.ASSIGNED)
        uiState.postValue(BaseUiState(SupportTicketState.TicketList(response)))
    }

    fun createTicket(supportTicket: AssistanceTicketRequest) = executeWithProgress {
        if (supportTicket.isValid()) {
            repository.createTicket(supportTicket)
            uiState.postValue(BaseUiState(SupportTicketState.TicketCreated))
        } else {
            uiState.postValue(BaseUiState(SupportTicketState.FormError("Los Datos son incorrectos")))
        }
    }

    fun findSubscriptionByNames(names: String) = executeNoProgress {
        val response = repository.findSubscriptionByNames(names)
        uiState.postValue(BaseUiState(SupportTicketState.SearchSubscriptionResult(response)))
    }

}


sealed class SupportTicketState {
    object Empty : SupportTicketState()
    data class UpdatedTicket(val ticket: AssistanceTicketResponse) : SupportTicketState()

    data class Success(val ticket: AssistanceTicketResponse) : SupportTicketState()

    data class TicketList(val ticketList: List<AssistanceTicketResponse>) : SupportTicketState()
    data class FormError(val error: String) : SupportTicketState()
    data class SearchSubscriptionResult(val response: List<SubscriptionFastSearchResponse>) :
        SupportTicketState()

    object TicketCreated : SupportTicketState()

}

