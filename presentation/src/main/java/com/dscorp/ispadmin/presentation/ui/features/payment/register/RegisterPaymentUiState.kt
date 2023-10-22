package com.dscorp.ispadmin.presentation.ui.features.payment.register

import com.example.cleanarchitecture.domain.domain.entity.Payment

sealed interface RegisterPaymentUiState {
    class OnPaymentRegistered(val payment: Payment) : RegisterPaymentUiState
    object HideDiscountFields : RegisterPaymentUiState
}
