package com.dscorp.ispadmin.presentation.ui.features.networkdevice

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NetworkDeviceFormError(val message: String) {

    companion object {
        const val NAME_ERROR = "El nombre no puede estar vacío"
        const val PASSWORD_ERROR = "La contraseña no puede estar vacía"
        const val USERNAME_ERROR = "El usuario no puede estar vacío"
        const val ADDRESS_ERROR = "La dirección IP no puede estar vacía"
        const val DEVICE_TYPE_ERROR = "El tipo de dispositivo no puede estar vacío"
    }

    class OnEtNameError : NetworkDeviceFormError(NAME_ERROR)
    class OnEtPasswordError : NetworkDeviceFormError(PASSWORD_ERROR)
    class OnEtUserNameError : NetworkDeviceFormError(USERNAME_ERROR)
    class OnEtAddressError : NetworkDeviceFormError(ADDRESS_ERROR)
    class OnDeviceTypeError : NetworkDeviceFormError(DEVICE_TYPE_ERROR)

}