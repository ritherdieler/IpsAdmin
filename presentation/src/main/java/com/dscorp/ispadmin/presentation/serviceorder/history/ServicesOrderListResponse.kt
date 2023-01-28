package com.dscorp.ispadmin.presentation.serviceorder.history

import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class ServicesOrderListResponse {
    class OnServicesOrderListFound(val servicesOrderList: List<ServiceOrderResponse>) :
        ServicesOrderListResponse()

    class OnError(val error: Exception) : ServicesOrderListResponse()

}
