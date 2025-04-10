package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import com.example.cleanarchitecture.domain.entity.*


sealed class EditSubscriptionUiState {
    class EditFormDataFound(
        val plans: List<PlanResponse>,
        val subscription: SubscriptionResponse
    ) : EditSubscriptionUiState()

    class EditSubscriptionSuccess(val subscription: SubscriptionResponse) :
        EditSubscriptionUiState()

    class EditSubscriptionError(val error: String) : EditSubscriptionUiState()
    class FormDataError(val error: String) : EditSubscriptionUiState()
}
