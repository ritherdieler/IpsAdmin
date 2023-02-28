package com.dscorp.ispadmin.presentation.ui.features.networkdevice.networkdevicelist

import com.example.cleanarchitecture.domain.domain.entity.NetworkDeviceResponse

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NetworkDeviceListResponse {
    class OnNetworkDeviceListFound(val networkDeviceList: List<NetworkDeviceResponse>) :
        NetworkDeviceListResponse()

    class OnError(val error: Exception) : NetworkDeviceListResponse()
}
