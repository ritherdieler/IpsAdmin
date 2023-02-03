package com.dscorp.ispadmin.presentation.ui.features.networkdevice

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class CleanFormErrors() {
    object OnEtNameError : CleanFormErrors()
    object OnEtPasswordError : CleanFormErrors()
    object OnEtUserNameError : CleanFormErrors()
    object OnEtAddressError : CleanFormErrors()
    object OnDeviceTypeError : CleanFormErrors()

}
