package com.dscorp.ispadmin.presentation.ui.features.place

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class FormError(val message: String) {

    companion object {
        const val NAME_ERROR = "el nombre no puede estar vacio"
        const val ABBREVIATION_ERROR = "Este campo no puede estar vacio"
        const val LOCATION_ERROR = "Este campo no puede estar vacio"
        const val NAME_INVALID = "Este nombres es invalido"
    }
    class OnEtNameError : FormError(NAME_ERROR)
    class OnEtAbbreviationError : FormError(ABBREVIATION_ERROR)
    class OnEtLocationError : FormError(LOCATION_ERROR)
    class OnEtNameIsInvalidError : FormError(NAME_INVALID)
}
