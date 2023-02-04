package com.dscorp.ispadmin.presentation.ui.features.napbox

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class CleanFormErrors() {
    object OnEtCodeCleanError: CleanFormErrors()
    object OnEtAddressCleanError : CleanFormErrors()
    object OnEtLocationCleanError : CleanFormErrors()

}
