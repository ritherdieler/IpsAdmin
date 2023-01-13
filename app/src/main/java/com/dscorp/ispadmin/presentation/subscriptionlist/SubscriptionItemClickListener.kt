package com.dscorp.ispadmin.presentation.subscriptionlist

import com.dscorp.ispadmin.domain.entity.Subscription

interface SubscriptionItemClickListener {
        fun onItemClick(subscription: Subscription)
    }


