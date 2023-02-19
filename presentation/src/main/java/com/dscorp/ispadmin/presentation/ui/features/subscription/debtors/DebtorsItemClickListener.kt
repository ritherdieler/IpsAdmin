package com.dscorp.ispadmin.presentation.ui.features.subscription.debtors

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

interface DebtorsItemClickListener {
    fun onItemClick(debtors: SubscriptionResponse)
}
