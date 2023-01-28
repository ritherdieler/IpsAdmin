package com.dscorp.ispadmin.presentation.serviceorder.register

import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterServiceOrderResponse{
    class ServiceOrderRegisterSuccess(val serviceOrder : ServiceOrder): RegisterServiceOrderResponse()
    class OnError(val error:Exception): RegisterServiceOrderResponse()
}
