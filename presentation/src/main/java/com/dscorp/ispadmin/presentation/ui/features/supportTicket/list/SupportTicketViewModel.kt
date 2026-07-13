package com.dscorp.ispadmin.presentation.ui.features.supportTicket.list

import android.content.Context
import android.net.Uri
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.data.response.AssistanceTicketResponse
import com.dscorp.ispadmin.data.response.AssistanceTicketStatus
import com.dscorp.ispadmin.domain.model.Place
import com.dscorp.ispadmin.domain.model.SubscriptionFastSearchResponse
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import com.dscorp.ispadmin.presentation.extension.firstDayFromCurrentMonth
import com.dscorp.ispadmin.presentation.extension.lastDayFromCurrentMonth
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.util.compressImage
import com.dscorp.ispadmin.presentation.util.rotateImageIfNeeded
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Calendar


class SupportTicketViewModel(
    private val repository: IRepository,
    private val context: Context,
    private val observabilityClient: ObservabilityClient
) : BaseViewModel<SupportTicketState>() {

    private companion object {
        const val OBS_FEATURE = "support_ticket"
        const val OBS_SCREEN = "support_ticket"
    }

    val placesFlow = MutableStateFlow<List<Place>>(value = emptyList())

    val user = repository.getUserSession()!!

    init {
        getPlaces()
    }

    private fun getPlaces() = executeNoProgress {
        try {
            val response = repository.getPlaces()
            placesFlow.value = response
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar lugares de tickets de soporte",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_places")
            )
            throw e
        }
    }

    fun getTicket(ticketId: String) = executeWithProgress {
        try {
            val response = repository.getTicket(ticketId)
            uiState.postValue(BaseUiState(SupportTicketState.Success(response)))
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar ticket de soporte",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_ticket", "entityId" to ticketId)
            )
            throw e
        }
    }

    suspend fun takeTicket(id: Int) {
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.USER_ACTION,
            message = "$OBS_FEATURE.take",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to id)
        )
        val response =
            repository.assignSupportTicketToUser(id, AssistanceTicketStatus.ASSIGNED, user.id!!)
        uiState.postValue(BaseUiState(SupportTicketState.UpdatedTicket(response)))
    }

    fun getFileFromUri(context: Context, fileUri: Uri): File? {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        val tempFile: File
        try {
            tempFile = File.createTempFile("tempFile", null, context.cacheDir)
            inputStream = context.contentResolver.openInputStream(fileUri)
            outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return tempFile
    }

    suspend fun closeTicket(ticket: AssistanceTicketResponse, installationSheetUri: Uri) {
        val file = getFileFromUri(context, installationSheetUri)
        file?.let {
            rotateImageIfNeeded(context, it, installationSheetUri)?.compressImage(50)?.apply {
                val response = repository.closeTicket(
                    id = ticket.id,
                    newStatus = AssistanceTicketStatus.CLOSED,
                    userId = user.id!!,
                    imageBase64 = this
                )
                uiState.postValue(BaseUiState(SupportTicketState.UpdatedTicket(response)))
            }
        }
    }

    suspend fun closeUnattendedTicket(ticket: AssistanceTicketResponse) {

        val response = repository.closeUnattendedTicket(
            id = ticket.id,
            newStatus = AssistanceTicketStatus.CANCELLED,
            userId = user.id!!,
        )

        uiState.postValue(BaseUiState(SupportTicketState.UpdatedTicket(response)))
    }

    fun getClosedTickets() = executeWithProgress {
        try {
            val firstDayOfMonth = Calendar.getInstance().firstDayFromCurrentMonth()
            val lastDayOfMonth = Calendar.getInstance().lastDayFromCurrentMonth()
            val response = repository.getTicketsByDateRange(
                AssistanceTicketStatus.CLOSED,
                firstDayOfMonth,
                lastDayOfMonth
            )
            uiState.postValue(BaseUiState(SupportTicketState.TicketList(response)))
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar tickets cerrados",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_closed")
            )
            throw e
        }
    }

    fun getPendingTickets() = executeWithProgress {
        try {
            val response = repository.getTicketsByStatus(AssistanceTicketStatus.PENDING)
            uiState.postValue(BaseUiState(SupportTicketState.TicketList(response)))
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar tickets pendientes",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_pending")
            )
            throw e
        }
    }

    fun getTakenTickets() = executeWithProgress {
        try {
            val response = repository.getTicketsByStatus(AssistanceTicketStatus.ASSIGNED)
            uiState.postValue(BaseUiState(SupportTicketState.TicketList(response)))
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar tickets asignados",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_taken")
            )
            throw e
        }
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

