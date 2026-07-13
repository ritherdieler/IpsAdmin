package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.Payment
import com.dscorp.ispadmin.domain.model.ServiceStatus
import com.dscorp.ispadmin.domain.usecase.service.ReactivateServiceUseCase
import com.dscorp.ispadmin.domain.usecase.service.RestoreInternetConnectionUseCase
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentHistoryState(
    val isLoading: Boolean = false,
    val payments: List<Payment> = emptyList(),
    val error: String? = null,
    val isReactivationButtonLoading: Boolean = false,
    val isServiceReactivated: Boolean = false,
    val reactivationNotes: String = "",
    val isRestoreInternetLoading: Boolean = false,
    val isInternetRestored: Boolean = false
)

class PaymentHistoryViewModel(
    val repository: IRepository,
    private val reactivateServiceUseCase: ReactivateServiceUseCase,
    private val restoreInternetConnectionUseCase: RestoreInternetConnectionUseCase,
    private val observabilityClient: ObservabilityClient
) : BaseViewModel<PaymentHistoryUiState>() {
    companion object {
        const val LAST_PAYMENTS_ROW_LIMIT = 10
        private const val OBS_FEATURE = "payment"
        private const val OBS_SCREEN = "payment_history"
    }

    private val _state = MutableStateFlow(PaymentHistoryState())
    val state: StateFlow<PaymentHistoryState> = _state.asStateFlow()

    // Keeping this for backward compatibility with the BaseViewModel
    val reactivationButtonIsLoading = MutableLiveData(false)

    // Store the original unfiltered list of payments
    private var allPayments: List<Payment> = emptyList()

    var subscriptionId: Int? = null
    
    // Añadimos la propiedad serviceStatus
    var serviceStatus: ServiceStatus = ServiceStatus.ACTIVE

    fun getLastPayments(itemsLimit: Int) = viewModelScope.launch {
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.NAVIGATION,
            message = "$OBS_FEATURE.load_history",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to subscriptionId, "itemsLimit" to itemsLimit)
        )
        try {
            _state.update { it.copy(isLoading = true) }
            val response = repository.getRecentPaymentsHistory(subscriptionId!!, itemsLimit)
            allPayments = response // Store the original list
            _state.update { it.copy(isLoading = false, payments = response) }

            // For backward compatibility
            uiState.value =
                BaseUiState(PaymentHistoryUiState.GetRecentPaymentsHistoryResponse())
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar historial de pagos",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_history", "entityId" to subscriptionId)
            )
            _state.update { it.copy(isLoading = false, error = e.message) }
            uiState.value =
                BaseUiState(PaymentHistoryUiState.GetRecentPaymentsHistoryError(e.message))
        }
    }

    fun showOnlyPendingPayments() = viewModelScope.launch {
        try {
            val pendingPayments = allPayments.filter { !it.paid }
            _state.update { it.copy(payments = pendingPayments) }

            // For backward compatibility
            uiState.value =
                BaseUiState(PaymentHistoryUiState.OnPaymentHistoryFilteredResponse())
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message) }
            uiState.value = BaseUiState(PaymentHistoryUiState.OnError(e.message))
        }
    }

    fun showAllPayments() = viewModelScope.launch {
        try {
            // Use the stored original list
            _state.update { it.copy(payments = allPayments) }

            // For backward compatibility
            uiState.value =
                BaseUiState(PaymentHistoryUiState.OnPaymentHistoryFilteredResponse())
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message) }
            uiState.value = BaseUiState(PaymentHistoryUiState.OnError(e.message))
        }
    }

    fun updateReactivationNotes(notes: String) {
        _state.update { it.copy(reactivationNotes = notes) }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearReactivationState() {
        _state.update { it.copy(isServiceReactivated = false) }
    }

    fun reactivateService() = viewModelScope.launch {
        _state.update { it.copy(isReactivationButtonLoading = true) }
        reactivationButtonIsLoading.value = true

        subscriptionId?.let { id ->
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.reactivate",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to id)
            )
            reactivateServiceUseCase(id, _state.value.reactivationNotes).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isReactivationButtonLoading = false,
                            isServiceReactivated = true,
                            reactivationNotes = "" // Clear notes after successful reactivation
                        )
                    }
                    uiState.value = BaseUiState(PaymentHistoryUiState.ServiceReactivated)
                    reactivationButtonIsLoading.value = false
                },
                onFailure = { error ->
                    observabilityClient.reportError(
                        throwable = error,
                        message = "Fallo al reactivar servicio desde historial de pagos",
                        tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "reactivate", "entityId" to id)
                    )
                    _state.update { 
                        it.copy(
                            isReactivationButtonLoading = false, 
                            error = error.message
                        ) 
                    }
                    uiState.value = BaseUiState(PaymentHistoryUiState.OnError(error.message))
                    reactivationButtonIsLoading.value = false
                }
            )
        }
    }

    fun restoreInternetConnection() = viewModelScope.launch {
        _state.update { it.copy(isRestoreInternetLoading = true) }

        subscriptionId?.let { id ->
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.USER_ACTION,
                message = "$OBS_FEATURE.restore_internet",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to id)
            )
            restoreInternetConnectionUseCase(id).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isRestoreInternetLoading = false,
                            isInternetRestored = true
                        )
                    }
                    uiState.value = BaseUiState(PaymentHistoryUiState.InternetRestored)
                },
                onFailure = { error ->
                    observabilityClient.reportError(
                        throwable = error,
                        message = "Fallo al restaurar conexión de internet",
                        tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "restore_internet", "entityId" to id)
                    )
                    _state.update {
                        it.copy(
                            isRestoreInternetLoading = false,
                            error = error.message
                        )
                    }
                    uiState.value = BaseUiState(PaymentHistoryUiState.OnError(error.message))
                }
            )
        }
    }

    fun clearInternetRestoredState() {
        _state.update { it.copy(isInternetRestored = false) }
    }
} 