package com.dscorp.ispadmin.presentation.serviceorden

import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class ServiceOrderResponse{
    class OnServiceOrderRegistered(val serviceOrder : ServiceOrder):ServiceOrderResponse()
    class OnError(val error:Exception):ServiceOrderResponse()
}
