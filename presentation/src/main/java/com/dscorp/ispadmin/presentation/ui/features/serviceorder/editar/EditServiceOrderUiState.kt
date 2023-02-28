package com.dscorp.ispadmin.presentation.ui.features.serviceorder.editar

import com.dscorp.ispadmin.presentation.ui.features.serviceorder.register.RegisterServiceOrderUiState
import com.example.cleanarchitecture.domain.domain.entity.*

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class EditServiceOrderUiState {

    class FetchFormDataError(val error: String) : EditServiceOrderUiState()

    class ServiceOrderEditSuccessOrder(val serviceOrder: ServiceOrderResponse) : EditServiceOrderUiState()
    class ServiceOrderEditErrorOrder(val error: String?) : EditServiceOrderUiState()
}
