package com.dscorp.ispadmin.presentation.subscriptionlist

import com.dscorp.ispadmin.presentation.subscription.SubscriptionResponse
import com.dscorp.ispadmin.repository.model.Subscription
import java.lang.Error

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class SubscriptionsListResponse {
    class OnSubscriptionFound(val subscriptions: List<Subscription>) : SubscriptionsListResponse()
    class OnError(val error: Exception) : SubscriptionsListResponse()

}
