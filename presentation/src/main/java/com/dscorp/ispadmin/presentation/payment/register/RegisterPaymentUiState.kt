package com.dscorp.ispadmin.presentation.payment.register

import com.example.cleanarchitecture.domain.domain.entity.Payment

sealed interface RegisterPaymentUiState {
    class OnPaymentRegistered(val payment: Payment) : RegisterPaymentUiState
    class OnError(val message: String?) : RegisterPaymentUiState

}
