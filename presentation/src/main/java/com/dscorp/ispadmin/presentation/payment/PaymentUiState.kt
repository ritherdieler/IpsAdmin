package com.dscorp.ispadmin.presentation.payment

import com.example.cleanarchitecture.domain.domain.entity.Payment

sealed interface PaymentUiState {
    class OnPaymentResponse(val payments: List<Payment>) : PaymentUiState
    class OnError(val message: String? = "Unknown Error") : PaymentUiState

}
