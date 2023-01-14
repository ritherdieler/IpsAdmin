package com.dscorp.ispadmin.presentation.subscriptionlist

import com.example.cleanarchitecture.domain.domain.entity.Subscription

interface SubscriptionItemClickListener {
        fun onItemClick(subscription: Subscription)
    }


