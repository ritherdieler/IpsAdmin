package com.dscorp.ispadmin.presentation.ui.features.subscription.debtors

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

sealed interface DebtorsUiState {
    class GetDebtorsSuccess(val debtors:List<SubscriptionResponse>): DebtorsUiState
    class GetDebtorsError(val message: String?) : DebtorsUiState
}
