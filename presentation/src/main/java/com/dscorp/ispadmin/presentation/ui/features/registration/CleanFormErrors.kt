package com.dscorp.ispadmin.presentation.ui.features.registration

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class CleanFormErrors() {
    object OnEtUserCleanError : CleanFormErrors()
    object OnEtFirstNameCleanError : CleanFormErrors()
    object OnEtLastNameCleanError : CleanFormErrors()
    object OnEtPassword1CleanError : CleanFormErrors()
    object OnEtPassword2CleanError : CleanFormErrors()

}
