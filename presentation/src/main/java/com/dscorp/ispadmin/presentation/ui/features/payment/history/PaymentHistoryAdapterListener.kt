package com.dscorp.ispadmin.presentation.ui.features.payment.history

import com.example.cleanarchitecture.domain.entity.Payment

interface PaymentHistoryAdapterListener {

    fun onPaymentHistoryItemClicked(payment: Payment)
}
