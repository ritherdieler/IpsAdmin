package com.dscorp.ispadmin.presentation.ui.features.technician

/**
 * Created by Sergio Carrillo Diestra on 26/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianFromError(val error:String) {
    companion object{
        const val FIRST_NAME_ERROR="Este campo no puede estar vacio"
        const val LAST_NAME_ERROR="Este campo no puede estar vacio"
        const val DNI_ERROR="Este campo no puede estar vacio"
        const val Type_ERROR="Este campo no puede estar vacio"
        const val USER_NAME_ERROR="Este campo no puede estar vacio"
        const val PASSWORD_ERROR="Este campo no puede estar vacio"
        const val ADDRESS_ERROR="Este campo no puede estar vacio"
        const val PHONE_ERROR="Este campo no puede estar vacio"
        const val BIRTHDAY_ERROR="Este campo no puede estar vacio"
    }
    class OnEtFirstNameError : TechnicianFromError(FIRST_NAME_ERROR)
    class OnEtLastNameError : TechnicianFromError(LAST_NAME_ERROR)
    class OnEtDniError : TechnicianFromError(DNI_ERROR)
    class OnEtTypeError : TechnicianFromError(Type_ERROR)
    class OnEtUserNameError : TechnicianFromError(USER_NAME_ERROR)
    class OnEtPasswordError : TechnicianFromError(PASSWORD_ERROR)
    class OnEtAddressError : TechnicianFromError(ADDRESS_ERROR)
    class OnEtPhoneError : TechnicianFromError(PHONE_ERROR)
    class OnEtBirthdayError : TechnicianFromError(BIRTHDAY_ERROR)
}

