package com.dscorp.ispadmin.presentation.subscriptionfinder

import com.example.cleanarchitecture.domain.domain.entity.Subscription

sealed interface FindSubscriptionUiState {
    class OnSubscriptionFound(val subscriptions: List<Subscription>) : FindSubscriptionUiState
    class OnError(val message: String?) : FindSubscriptionUiState


}
