package com.dscorp.ispadmin.presentation.subscriptionlist

import com.dscorp.ispadmin.repository.model.Subscription

interface SubscriptionItemClickListener {
        fun onItemClick(subscription: Subscription)
    }


