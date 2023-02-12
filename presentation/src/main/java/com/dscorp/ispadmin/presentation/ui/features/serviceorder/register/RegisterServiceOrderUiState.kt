package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterServiceOrderUiState{
    class ServiceOrderRegisterSuccessOrder(val serviceOrder : ServiceOrder): RegisterServiceOrderUiState()
    class ServiceOrderRegisterErrorOrder(val error:Exception): RegisterServiceOrderUiState()

}
