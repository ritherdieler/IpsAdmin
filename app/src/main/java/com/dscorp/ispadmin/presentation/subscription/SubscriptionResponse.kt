package com.dscorp.ispadmin.presentation.subscription

import com.dscorp.ispadmin.repository.model.NetworkDevice
import com.dscorp.ispadmin.repository.model.Plan
import com.dscorp.ispadmin.repository.model.Subscription

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class SubscriptionResponse{
    class OnPlansFetched(val plans:List <Plan>): SubscriptionResponse()
    class OnNetworkDeviceFetched(val networkdevices:List<NetworkDevice>): SubscriptionResponse()
    class OnSubscriptionRegistered(val subscription:Subscription):SubscriptionResponse()
    class OnError(val error:Exception):SubscriptionResponse()
}
