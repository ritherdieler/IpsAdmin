package com.dscorp.ispadmin.presentation.ui.features.technician

/**
 * Created by Sergio Carrillo Diestra on 26/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianErrorCleanForm {

    object OnEtFirstNameCleanError : TechnicianErrorCleanForm()
    object OnEtLastNameCleanError : TechnicianErrorCleanForm()
    object OnEtDniCleanError : TechnicianErrorCleanForm()
    object OnEtTypeCleanError: TechnicianErrorCleanForm()
    object OnEtUserNameCleanError : TechnicianErrorCleanForm()
    object OnEtPasswordCleanError : TechnicianErrorCleanForm()
    object OnEtAddressCleanError : TechnicianErrorCleanForm()
    object OnEtPhoneCleanError : TechnicianErrorCleanForm()
    object OnEtBirthdayCleanError : TechnicianErrorCleanForm()
}

