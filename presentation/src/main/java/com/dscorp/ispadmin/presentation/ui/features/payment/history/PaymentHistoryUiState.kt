package com.dscorp.ispadmin.presentation.ui.features.payment.history

import com.example.cleanarchitecture.domain.entity.Payment

sealed interface PaymentHistoryUiState {
    class OnPaymentHistoryFilteredResponse(val payments: List<Payment>) : PaymentHistoryUiState
    class OnError(val message: String? = "Unknown Error") : PaymentHistoryUiState
    class GetRecentPaymentsHistoryResponse(val payments: List<Payment>) : PaymentHistoryUiState
    class GetRecentPaymentsHistoryError(val message: String? = "Unknown Error") : PaymentHistoryUiState
    object ServiceReactivated:PaymentHistoryUiState

}
