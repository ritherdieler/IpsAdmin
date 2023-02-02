package com.dscorp.ispadmin.presentation.ui.features.technician

/**
 * Created by Sergio Carrillo Diestra on 26/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianErrorCleanForm {

    object OnEtFirstNameError : TechnicianErrorCleanForm()
    object OnEtLastNameError : TechnicianErrorCleanForm()
    object OnEtDniError : TechnicianErrorCleanForm()
    object OnEtTypeError: TechnicianErrorCleanForm()
    object OnEtUserNameError : TechnicianErrorCleanForm()
    object OnEtPasswordError : TechnicianErrorCleanForm()
    object OnEtAddressError : TechnicianErrorCleanForm()
    object OnEtPhoneError : TechnicianErrorCleanForm()
    object OnEtBirthdayError : TechnicianErrorCleanForm()
}

