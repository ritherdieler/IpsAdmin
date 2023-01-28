package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.view.View
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

interface SelectableSubscriptionListener {
    fun onSubscriptionSelected(subscription: SubscriptionResponse, view: View)

}
