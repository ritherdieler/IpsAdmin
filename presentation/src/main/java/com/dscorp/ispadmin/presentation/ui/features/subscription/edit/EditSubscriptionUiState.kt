package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

sealed class EditSubscriptionUiState {

    class EditSubscriptionSuccess(val subscription: SubscriptionResponse) :
        EditSubscriptionUiState()

    class EditSubscriptionError(val error:String?) : EditSubscriptionUiState()
    object Idle : EditSubscriptionUiState()

}
