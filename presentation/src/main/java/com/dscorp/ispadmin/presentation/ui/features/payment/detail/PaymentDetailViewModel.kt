package com.dscorp.ispadmin.presentation.ui.features.payment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.domain.model.Payment
import com.dscorp.ispadmin.domain.usecase.payment.GetPaymentByIdUseCase
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentDetailViewModel(
    private val getPaymentByIdUseCase: GetPaymentByIdUseCase,
    private val observabilityClient: ObservabilityClient
) : ViewModel() {

    private companion object {
        const val OBS_FEATURE = "payment"
        const val OBS_SCREEN = "payment_detail"
    }

    private val _uiState = MutableStateFlow(PaymentDetailUiState())
    val uiState: StateFlow<PaymentDetailUiState> = _uiState.asStateFlow()

    fun fetchPaymentDetails(paymentId: String) {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.NAVIGATION,
                message = "$OBS_FEATURE.load_detail",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to paymentId)
            )
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getPaymentByIdUseCase(paymentId).fold(
                onSuccess = { payment ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            payment = payment,
                            error = null
                        )
                    }
                },
                onFailure = { throwable ->
                    observabilityClient.reportError(
                        throwable = throwable,
                        message = "Fallo al cargar detalle de pago",
                        tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_detail", "entityId" to paymentId)
                    )
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Error al cargar los detalles del pago"
                        )
                    }
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class PaymentDetailUiState(
    val isLoading: Boolean = false,
    val payment: Payment? = null,
    val error: String? = null
) 