package com.dscorp.ispadmin.presentation.networkdevice

import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NetworkDeviceResponse {
    class OnNetworkDeviceRegistered(val networkDevice: NetworkDevice) : NetworkDeviceResponse()
    class OnError(val error: Exception) : NetworkDeviceResponse()
}
