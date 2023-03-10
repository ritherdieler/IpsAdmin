package com.dscorp.ispadmin.presentation.serviceorden

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class ServiceOrderFormError {
    class OnEtLatitudError(val error: String) : ServiceOrderFormError()
    class OnEtLogintudError(val error: String) : ServiceOrderFormError()
    class OnEtCreateDateError(val error: String) : ServiceOrderFormError()
    class OnEtAttentionDate(val error: String) : ServiceOrderFormError()
}
