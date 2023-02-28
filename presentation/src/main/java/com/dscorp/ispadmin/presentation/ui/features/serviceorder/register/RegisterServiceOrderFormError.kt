package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterServiceOrderFormError(val error: String) {

    companion object {
        const val LOCATION_ERROR = "Debe seleccionar una ubicacion"
        const val ISSUE_ERROR = "Debe ingresar un problema"
        const val SUBSCRIPTION_ERROR = "Ocurrio un error, por favor contacte con soporte tecnico"
    }

    class OnEtIssueError : RegisterServiceOrderFormError(ISSUE_ERROR)
    class OnSubscriptionError : RegisterServiceOrderFormError(SUBSCRIPTION_ERROR)

    class GenericError : RegisterServiceOrderFormError(SUBSCRIPTION_ERROR)
}
