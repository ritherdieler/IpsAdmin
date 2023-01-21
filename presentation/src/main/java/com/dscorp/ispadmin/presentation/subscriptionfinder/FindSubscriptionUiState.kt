package com.dscorp.ispadmin.presentation.subscriptionfinder

import com.example.cleanarchitecture.domain.domain.entity.Subscription

sealed class FindSubscriptionUiState {
    class OnSubscriptionFound(val subscriptions: List<Subscription>) : FindSubscriptionUiState()

}
