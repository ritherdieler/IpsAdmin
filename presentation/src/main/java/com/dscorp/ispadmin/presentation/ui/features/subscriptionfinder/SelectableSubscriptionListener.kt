package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.view.View
import com.example.cleanarchitecture.domain.entity.SubscriptionResponse

interface SelectableSubscriptionListener {
    fun onSubscriptionPopupButtonSelected(subscription: SubscriptionResponse, view: View)
}
