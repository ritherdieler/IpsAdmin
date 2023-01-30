package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import com.example.cleanarchitecture.domain.domain.entity.*

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionResponse {
    class OnFormDataFound(
        val plans: List<Plan>,
        val networkDevice: List<NetworkDevice>,
        val places: List<Place>,
        val technicians: List<Technician>,
        val napBoxs: List<NapBox>
    ) :
        RegisterSubscriptionResponse()

    class OnRegisterSubscriptionRegistered(val subscription: Subscription) : RegisterSubscriptionResponse()
    class OnError(val error: Exception) : RegisterSubscriptionResponse()
}
