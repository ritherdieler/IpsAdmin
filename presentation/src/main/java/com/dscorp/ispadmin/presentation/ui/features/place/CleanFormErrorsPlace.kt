package com.dscorp.ispadmin.presentation.ui.features.place

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class CleanFormErrorsPlace {
    object OnEtNamePlaceCleanError : CleanFormErrorsPlace()
    object OnEtAbbreviationCleanError : CleanFormErrorsPlace()
}
