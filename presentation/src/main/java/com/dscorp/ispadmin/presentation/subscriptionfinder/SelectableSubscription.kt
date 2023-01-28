package com.dscorp.ispadmin.presentation.subscriptionfinder

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

interface SelectableSubscriptionListener {
    fun onSubscriptionSelected(subscription: SubscriptionResponse)

}
