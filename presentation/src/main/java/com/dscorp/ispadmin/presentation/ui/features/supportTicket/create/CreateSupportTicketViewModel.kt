package com.dscorp.ispadmin.presentation.ui.features.supportTicket.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.SubscriptionFastSearchResponse
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateSupportTicketViewModel(
    private val repository: IRepository,
) : ViewModel() {

    // Estado principal de la pantalla usando UDF
    private val _uiState = MutableStateFlow(CreateSupportTicketUiState())
    val uiState: StateFlow<CreateSupportTicketUiState> = _uiState.asStateFlow()

    // Categorías para los tickets de soporte
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

    // Inicializar los datos al crear el ViewModel
    init {
        getPlaces()
    }

    // Obtener lugares disponibles
    private fun getPlaces() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val response = repository.getPlaces()
                _uiState.update { 
                    it.copy(
                        places = response,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                // Manejar el error sin afectar el estado principal
                _uiState.update { 
                    it.copy(
                        error = "Error al cargar lugares: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }

    // Buscar suscripciones por nombre
    fun findSubscriptionByNames(names: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val response = repository.findSubscriptionByNames(names)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subscriptions = response
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al buscar suscripciones: ${e.message}"
                    )
                }
            }
        }
    }

    // Crear un ticket de soporte
    fun createTicket(ticketRequest: AssistanceTicketRequest) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                if (ticketRequest.isValid()) {
                    repository.createTicket(ticketRequest)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isTicketCreated = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Los datos son incorrectos"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al crear ticket: ${e.message}"
                    )
                }
            }
        }
    }

    // Resetear el error
    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

    // Resetear el estado de ticketCreated
    fun resetTicketCreated() {
        _uiState.update { it.copy(isTicketCreated = false) }
    }
}

// Estado de la UI siguiendo UDF
data class CreateSupportTicketUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isTicketCreated: Boolean = false,
    val subscriptions: List<SubscriptionFastSearchResponse> = emptyList(),
    val places: List<PlaceResponse> = emptyList()
) 