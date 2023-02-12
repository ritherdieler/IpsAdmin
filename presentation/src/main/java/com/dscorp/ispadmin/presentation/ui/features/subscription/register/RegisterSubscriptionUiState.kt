package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import com.example.cleanarchitecture.domain.domain.entity.*

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionUiState {
    class FormDataFound(
        val plans: List<Plan>,
        val networkDevices: List<NetworkDevice>,
        val places: List<Place>,
        val technicians: List<Technician>,
        val napBoxes: List<NapBox>,
        val hostNetworkDevices: List<NetworkDevice>
    ) : RegisterSubscriptionUiState()

    class RegisterSubscriptionSuccess(val subscription: Subscription) :
        RegisterSubscriptionUiState()

    class RegisterSubscriptionError(val error: String) : RegisterSubscriptionUiState()
    class FormDataError(val error: String) : RegisterSubscriptionUiState()
}
