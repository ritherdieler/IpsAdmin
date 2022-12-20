package com.dscorp.ispadmin.presentation.place

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class FormError{
    class OnEtNamePlaceError(val error:String): FormError()
    class OnEtAbbreviationError(val error: String): FormError()
}
