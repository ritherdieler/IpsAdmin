package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import com.dscorp.ispadmin.presentation.ui.features.dashboard.DashBoardDataUiState
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.cleanarchitecture.domain.domain.entity.Onu


sealed class EditSubscriptionUiState {
    class EditFormDataFound(
        val plans: List<PlanResponse>
    ) : EditSubscriptionUiState()

    class EditSubscriptionSuccess(val subscription: SubscriptionResponse) :EditSubscriptionUiState()

    class EditSubscriptionError(val error: String) : EditSubscriptionUiState()
    class FormDataError(val error: String) : EditSubscriptionUiState()


}
