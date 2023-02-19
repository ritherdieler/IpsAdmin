package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import com.example.cleanarchitecture.domain.domain.entity.*

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class EditSubscriptionUiState {
    class FormDataFound(
        val plans: List<Plan>,
        val networkDevices: List<NetworkDevice>,
        val places: List<Place>,
        val napBoxes: List<NapBox>,
        val hostNetworkDevices: List<NetworkDevice>
    ) : EditSubscriptionUiState()

    class FetchFormDataError(val error: String) : EditSubscriptionUiState()

    class EditSubscriptionSuccess(val subscription: SubscriptionResponse) : EditSubscriptionUiState()
    class EditSubscriptionError(val error: String?) : EditSubscriptionUiState()
}
