package com.dscorp.ispadmin.presentation.subscriptionfinder

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

sealed interface FindSubscriptionUiState {
    class OnSubscriptionFound(val subscriptions: List<SubscriptionResponse>) :
        FindSubscriptionUiState

    class OnError(val message: String?) : FindSubscriptionUiState


}
