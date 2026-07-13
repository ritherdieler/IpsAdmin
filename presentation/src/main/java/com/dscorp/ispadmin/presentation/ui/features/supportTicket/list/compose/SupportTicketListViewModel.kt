package com.dscorp.ispadmin.presentation.ui.features.supportTicket.list.compose

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.data.response.AssistanceTicketResponse
import com.dscorp.ispadmin.data.response.AssistanceTicketStatus
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import com.dscorp.ispadmin.presentation.extension.firstDayFromCurrentMonth
import com.dscorp.ispadmin.presentation.extension.lastDayFromCurrentMonth
import com.dscorp.ispadmin.presentation.util.compressImage
import com.dscorp.ispadmin.presentation.util.rotateImageIfNeeded
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Calendar
import com.dscorp.ispadmin.data.apirequestmodel.RescheduleTicketRequest

class SupportTicketListViewModel(
    private val repository: IRepository,
    private val context: Context,
    private val observabilityClient: ObservabilityClient
) : ViewModel() {

    private companion object {
        const val OBS_FEATURE = "support_ticket"
        const val OBS_SCREEN = "support_ticket_list"
    }

    private val _uiState = MutableStateFlow(SupportTicketListUiState())
    val uiState: StateFlow<SupportTicketListUiState> = _uiState.asStateFlow()
    
    init {
        loadUserData()
        loadPendingTickets()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            try {
                repository.getUserSession()?.let { user ->
                    _uiState.update { it.copy(user = user) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al cargar datos del usuario") }
            }
        }
    }
    
    fun onTabChange(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
        loadTicketsForActiveTab()
    }

    fun onDateFilterChange(filter: TicketDateFilter) {
        if (_uiState.value.selectedDateFilter == filter) return

        _uiState.update { currentState ->
            currentState.copy(
                selectedDateFilter = filter
            )
        }
    }

    fun onSortOptionChange(sortOption: TicketSortOption) {
        if (_uiState.value.selectedSortOption == sortOption) return

        _uiState.update { currentState ->
            currentState.copy(
                selectedSortOption = sortOption
            )
        }
    }
    
    fun refreshData() {
        loadTicketsForActiveTab()
    }
    
    private fun loadTicketsForActiveTab() {
        when (_uiState.value.activeTab) {
            0 -> loadPendingTickets()
            1 -> loadInProgressTickets()
            2 -> loadClosedTickets()
        }
    }
    
    private fun loadPendingTickets() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val pendingTickets = repository.getTicketsByStatus(AssistanceTicketStatus.PENDING)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        pendingTickets = pendingTickets,
                        error = null
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cargar tickets pendientes",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_pending")
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar tickets pendientes"
                    )
                }
            }
        }
    }
    
    private fun loadInProgressTickets() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val inProgressTickets = repository.getTicketsByStatus(AssistanceTicketStatus.ASSIGNED)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        inProgressTickets = inProgressTickets,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cargar tickets en progreso",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_in_progress")
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar tickets en progreso"
                    )
                }
            }
        }
    }
    
    private fun loadClosedTickets() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val firstDayOfMonth = Calendar.getInstance().firstDayFromCurrentMonth()
                val lastDayOfMonth = Calendar.getInstance().lastDayFromCurrentMonth()
                val closedTickets = repository.getTicketsByDateRange(
                    AssistanceTicketStatus.CLOSED,
                    firstDayOfMonth,
                    lastDayOfMonth
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        closedTickets = closedTickets,
                        error = null
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cargar tickets cerrados",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_closed")
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar tickets cerrados"
                    )
                }
            }
        }
    }
    
    fun takeTicket(ticketId: Int) {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.take",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to ticketId)
            )
            try {
                _uiState.update { 
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            put(ticketId, true)
                        }
                    )
                }
                
                val user = _uiState.value.user ?: return@launch
                val updatedTicket = runCatching {
                    repository.assignSupportTicketToUser(
                        ticketId,
                        AssistanceTicketStatus.ASSIGNED,
                        user.id!!
                    )
                }.getOrThrow()
                
                // Actualizar los tickets pendientes y en progreso
                refreshData()

                
                _uiState.update { 
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            remove(ticketId)
                        }
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al tomar ticket",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "take", "entityId" to ticketId)
                )
                _uiState.update { 
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            remove(ticketId)
                        },
                        error = e.message ?: "Error al tomar el ticket"
                    )
                }
            }
        }
    }
    
    fun closeUnattendedTicket(ticket: AssistanceTicketResponse) {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.close_unattended",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to ticket.id)
            )
            try {
                _uiState.update { 
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            put(ticket.id, true)
                        }
                    )
                }
                
                val user = _uiState.value.user ?: return@launch
                val updatedTicket = runCatching {
                    repository.closeUnattendedTicket(
                        ticket.id,
                        AssistanceTicketStatus.CANCELLED,
                        user.id!!
                    )
                }.getOrThrow()
                
                refreshData()
                
                _uiState.update { 
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        }
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cancelar ticket no atendido",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "close_unattended", "entityId" to ticket.id)
                )
                _uiState.update { 
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        },
                        error = e.message ?: "Error al cancelar el ticket"
                    )
                }
            }
        }
    }

    fun rescheduleTicket(ticket: AssistanceTicketResponse, scheduledAt: Long) {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.reschedule",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to ticket.id, "scheduledAt" to scheduledAt)
            )
            try {
                _uiState.update {
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            put(ticket.id, true)
                        },
                        inProgressTicketsLoading = it.inProgressTicketsLoading.toMutableMap().apply {
                            put(ticket.id, true)
                        },
                        error = null
                    )
                }

                repository.rescheduleTicket(
                    ticketId = ticket.id,
                    request = RescheduleTicketRequest(scheduledAt)
                )

                refreshData()

                _uiState.update {
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        },
                        inProgressTicketsLoading = it.inProgressTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        },
                        successMessage = "Ticket reprogramado correctamente",
                        error = null
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al reprogramar ticket",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "reschedule", "entityId" to ticket.id)
                )
                _uiState.update {
                    it.copy(
                        pendingTicketsLoading = it.pendingTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        },
                        inProgressTicketsLoading = it.inProgressTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        },
                        successMessage = null,
                        error = e.message ?: "Error al reprogramar el ticket"
                    )
                }
            }
        }
    }
    
    fun closeTicket(ticket: AssistanceTicketResponse, imageUri: Uri) {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.close",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to ticket.id)
            )
            try {
                _uiState.update { 
                    it.copy(
                        inProgressTicketsLoading = it.inProgressTicketsLoading.toMutableMap().apply {
                            put(ticket.id, true)
                        }
                    )
                }
                
                val user = _uiState.value.user ?: return@launch
                val file = getFileFromUri(context, imageUri)
                
                if (file != null) {
                    val rotatedAndCompressedImage = runCatching {
                        rotateImageIfNeeded(context, file, imageUri)?.compressImage(50)
                    }.getOrNull()
                    
                    if (rotatedAndCompressedImage != null) {
                        val updatedTicket = runCatching {
                            repository.closeTicket(
                                ticket.id,
                                AssistanceTicketStatus.CLOSED,
                                user.id!!,
                                rotatedAndCompressedImage
                            )
                        }.getOrThrow()
                        
                        refreshData()
                    } else {
                        _uiState.update { 
                            it.copy(
                                error = "Error al procesar la imagen"
                            )
                        }
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Error al obtener la imagen"
                        )
                    }
                }
                
                _uiState.update { 
                    it.copy(
                        inProgressTicketsLoading = it.inProgressTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        }
                    )
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cerrar ticket",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "close", "entityId" to ticket.id)
                )
                _uiState.update { 
                    it.copy(
                        inProgressTicketsLoading = it.inProgressTicketsLoading.toMutableMap().apply {
                            remove(ticket.id)
                        },
                        error = e.message ?: "Error al cerrar el ticket"
                    )
                }
            }
        }
    }
    
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun dismissSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
    
    private fun getFileFromUri(context: Context, fileUri: Uri): File? {
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
} 