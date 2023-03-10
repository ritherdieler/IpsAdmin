package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import com.dscorp.ispadmin.presentation.ui.features.dashboard.DashBoardDataUiState
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.cleanarchitecture.domain.domain.entity.Onu

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionUiState {
    class FormDataFound(
        val plans: List<PlanResponse>,
        val networkDevices: List<NetworkDevice>,
        val places: List<PlaceResponse>,
        val technicians: List<Technician>,
        val napBoxes: List<NapBoxResponse>,
        val hostNetworkDevices: List<NetworkDevice>,
        val unconfirmedOnus: List<Onu>
    ) : RegisterSubscriptionUiState()

    class FiberDevicesFound(val devices: List<NetworkDevice>) : RegisterSubscriptionUiState()

    class WirelessDevicesFound(val devices: List<NetworkDevice>) : RegisterSubscriptionUiState()

    class RegisterSubscriptionSuccess(val subscription: Subscription) :
        RegisterSubscriptionUiState()

    class RegisterSubscriptionError(val error: String) : RegisterSubscriptionUiState()
    class FormDataError(val error: String) : RegisterSubscriptionUiState()

    class LoadingData(val loading: Boolean) : RegisterSubscriptionUiState()
    class LoadingLogin(val loading: Boolean) : RegisterSubscriptionUiState()
}
