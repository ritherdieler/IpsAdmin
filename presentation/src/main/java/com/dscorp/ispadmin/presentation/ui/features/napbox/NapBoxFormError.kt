package com.dscorp.ispadmin.presentation.ui.features.napbox

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NapBoxFormError (val message: String){

    companion object {
        const val CODE_ERROR = "El codigo no puede estar vacio"
        const val ADDRESS_ERROR = "La dirección no puede estar vacio"
        const val LOCATION_ERROR = "La ubicación no puede estar vacio"
    }

    class OnEtCodeError : NapBoxFormError(CODE_ERROR)
    class OnEtAddressError : NapBoxFormError(ADDRESS_ERROR)
    class OnEtLocationError : NapBoxFormError(LOCATION_ERROR)

}
