package com.dscorp.ispadmin.presentation.ui.features.serviceorder.editar

import com.dscorp.ispadmin.presentation.ui.features.serviceorder.register.RegisterServiceOrderFormError

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class EditServiceOrderFormErrorUiState(val error: String? = null) {
    companion object {
        const val ISSUE_ERROR = "Debe ingresar un problema"
        const val SUBSCRIPTION_ERROR = "Ocurrio un error, por favor contacte con soporte tecnico"
    }

    class OnEtIssueError : EditServiceOrderFormErrorUiState(ISSUE_ERROR)
    class OnSubscriptionError : EditServiceOrderFormErrorUiState(SUBSCRIPTION_ERROR)

    class GenericError : EditServiceOrderFormErrorUiState(SUBSCRIPTION_ERROR)
}
