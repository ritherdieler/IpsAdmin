package com.dscorp.ispadmin.presentation.subscriptionlist

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

interface SubscriptionItemClickListener {
        fun onItemClick(subscription: SubscriptionResponse)
    }


