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
        const val PASSWORD_HAS_NOT_ERRORS = "Contraseña requiere entre 8 a 20 caracteres"
        const val IPV4_ADDRESS_HAS_NOT_ERRORS = "La Dirección de ip no es valida"
    }

    class OnEtNameError : NetworkDeviceFormError(NAME_ERROR)
    class OnEtPasswordError : NetworkDeviceFormError(PASSWORD_ERROR)
    class OnEtUserNameError : NetworkDeviceFormError(USERNAME_ERROR)
    class OnEtAddressError : NetworkDeviceFormError(ADDRESS_ERROR)
    class OnDeviceTypeError : NetworkDeviceFormError(DEVICE_TYPE_ERROR)
    class OnEtPasswordIsInvalidError: NetworkDeviceFormError(PASSWORD_HAS_NOT_ERRORS)
    class OnEtIpv4AddressIsInvalidError: NetworkDeviceFormError(IPV4_ADDRESS_HAS_NOT_ERRORS)

}
