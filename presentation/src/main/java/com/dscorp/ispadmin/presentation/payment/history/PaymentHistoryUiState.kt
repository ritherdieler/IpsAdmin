package com.dscorp.ispadmin.presentation.payment.history

import com.example.cleanarchitecture.domain.domain.entity.Payment

sealed interface PaymentHistoryUiState {
    class OnPaymentResponseHistory(val payments: List<Payment>) : PaymentHistoryUiState
    class OnError(val message: String? = "Unknown Error") : PaymentHistoryUiState

}
