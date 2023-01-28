package com.dscorp.ispadmin.presentation.ui.features.subscriptionlist

import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class SubscriptionsListResponse {
    class OnSubscriptionFound(val subscriptions: List<SubscriptionResponse>) : SubscriptionsListResponse()
    class OnError(val error: Exception) : SubscriptionsListResponse()

}
