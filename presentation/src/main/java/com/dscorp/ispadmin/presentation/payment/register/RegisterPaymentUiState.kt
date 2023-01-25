package com.dscorp.ispadmin.presentation.payment.register

import com.example.cleanarchitecture.domain.domain.entity.Payment

sealed interface RegisterPaymentUiState {
    class OnPaymentSuccess(val payment: Payment) : RegisterPaymentUiState
    class OnPaymentRegistered(response: Payment) : RegisterPaymentUiState


}
