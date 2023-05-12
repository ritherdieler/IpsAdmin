package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.Technician

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionUiState {
    class FormDataFound(
        val networkDevices: List<NetworkDevice>,
        val places: List<PlaceResponse>,
        val technicians: List<Technician>,
        val napBoxes: List<NapBoxResponse>,
        val hostNetworkDevices: List<NetworkDevice>,
        val unconfirmedOnus: List<Onu>
    ) : RegisterSubscriptionUiState()

    class OnOnuDataFound(val onus: List<Onu>) : RegisterSubscriptionUiState()

    class FiberDevicesFound(val devices: List<NetworkDevice>) : RegisterSubscriptionUiState()

    class WirelessDevicesFound(val devices: List<NetworkDevice>) : RegisterSubscriptionUiState()

    class PlansFound(val plans: List<PlanResponse>) : RegisterSubscriptionUiState()

    class RegisterSubscriptionSuccess(val subscription: Subscription) :
        RegisterSubscriptionUiState()

    class ShimmerVisibility(val showShimmer: Boolean) : RegisterSubscriptionUiState()
    class CouponIsValid(val isValid: Boolean) : RegisterSubscriptionUiState()
    class RefreshingOnus(val isRefreshing: Boolean) : RegisterSubscriptionUiState()
}
