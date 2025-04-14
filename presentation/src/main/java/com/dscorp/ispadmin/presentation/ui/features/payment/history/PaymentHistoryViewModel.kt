package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.cleanarchitecture.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.repository.IRepository
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
    val isServiceReactivated: Boolean = false
)

class PaymentHistoryViewModel(val repository: IRepository) :
    BaseViewModel<PaymentHistoryUiState>() {
    companion object {
        const val LAST_PAYMENTS_ROW_LIMIT = 10
    }

    private val _state = MutableStateFlow(PaymentHistoryState())
    val state: StateFlow<PaymentHistoryState> = _state.asStateFlow()

    // Keeping this for backward compatibility with the BaseViewModel
    val reactivationButtonIsLoading = MutableLiveData(false)

    // Store the original unfiltered list of payments
    private var allPayments: List<Payment> = emptyList()
    
    var subscriptionId: Int? = null

    fun getFilteredPaymentHistory(request: SearchPaymentsRequest) = viewModelScope.launch {
        try {
            _state.update { it.copy(isLoading = true) }
            val response = repository.getFilteredPaymentHistory(request)
            allPayments = response // Store the original list
            _state.update { it.copy(isLoading = false, payments = response) }
            
            // For backward compatibility
            uiState.value = BaseUiState(PaymentHistoryUiState.OnPaymentHistoryFilteredResponse(response))
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = e.message) }
            uiState.value = BaseUiState(PaymentHistoryUiState.OnError(e.message))
        }
    }

    fun getLastPayments(itemsLimit: Int) = viewModelScope.launch {
        try {
            _state.update { it.copy(isLoading = true) }
            val response = repository.getRecentPaymentsHistory(subscriptionId!!, itemsLimit)
            allPayments = response // Store the original list
            _state.update { it.copy(isLoading = false, payments = response) }
            
            // For backward compatibility
            uiState.value = BaseUiState(PaymentHistoryUiState.GetRecentPaymentsHistoryResponse(response))
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = e.message) }
            uiState.value = BaseUiState(PaymentHistoryUiState.GetRecentPaymentsHistoryError(e.message))
        }
    }

    fun showOnlyPendingPayments() = viewModelScope.launch {
        try {
            val pendingPayments = allPayments.filter { !it.paid }
            _state.update { it.copy(payments = pendingPayments) }
            
            // For backward compatibility
            uiState.value = BaseUiState(PaymentHistoryUiState.OnPaymentHistoryFilteredResponse(pendingPayments))
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
            uiState.value = BaseUiState(PaymentHistoryUiState.OnPaymentHistoryFilteredResponse(allPayments))
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message) }
            uiState.value = BaseUiState(PaymentHistoryUiState.OnError(e.message))
        }
    }

    fun reactivateService() = viewModelScope.launch {
        try {
            _state.update { it.copy(isReactivationButtonLoading = true) }
            reactivationButtonIsLoading.value = true
            
            subscriptionId?.let {
                repository.reactivateService(subscriptionId!!, repository.getUserSession()!!.id!!)
                _state.update { it.copy(isReactivationButtonLoading = false, isServiceReactivated = true) }
                uiState.value = BaseUiState(PaymentHistoryUiState.ServiceReactivated)
            }
        } catch (e: Exception) {
            _state.update { it.copy(isReactivationButtonLoading = false, error = e.message) }
            uiState.value = BaseUiState(PaymentHistoryUiState.OnError(e.message))
        } finally {
            reactivationButtonIsLoading.value = false
        }
    }
} 