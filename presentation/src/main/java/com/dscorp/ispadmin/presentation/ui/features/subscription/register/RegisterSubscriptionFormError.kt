package com.dscorp.ispadmin.presentation.ui.features.subscription.register

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionFormError(val error:String) {
    companion object{
        const val FIRST_NAME_ERROR ="Este campo no puede estar vacio"
        const val LAST_NAME_ERROR ="Este campo no puede estar vacio"
        const val DNI_ERROR ="Este campo no puede estar vacio"
        const val PASSWORD_ERROR ="Este campo no puede estar vacio"
        const val ADDRESS_ERROR ="Este campo no puede estar vacio"
        const val NUMBER_PHONE_ERROR ="Este campo no puede estar vacio"
        const val SUBSCRIPTION_DATE_ERROR ="Este campo no puede estar vacio"
        const val SPN_PLAN_ERROR ="Este campo no puede estar vacio"
        const val SPN_NETWORK_DEVICE_ERROR ="Este campo no puede estar vacio"
        const val SPN_PLACE_ERROR ="Este campo no puede estar vacio"
        const val SPN_TECHNICIAN_ERROR ="Este campo no puede estar vacio"
        const val SPN_NAP_BOX_ERROR ="Este campo no puede estar vacio"
        const val LOCATION_ERROR ="Este campo no puede estar vacio"
        const val DNI_IS_INVALID_ERROR ="DNI requiere 8 digitos"
        const val PASSWORD_IS_INVALID_ERROR ="Requiere entre 8 a 20 caracteres"
        const val PHONE_IS_INVALID_ERROR ="Requiere 9 digitos"
        const val FIRST_NAME_IS_INVALID_ERROR ="Este nombre no es valido"
        const val LAST_NAME_IS_INVALID_ERROR ="Este nombre no es valido"

    }
    class OnEtFirstNameError : RegisterSubscriptionFormError(FIRST_NAME_ERROR)
    class OnEtLastNameError : RegisterSubscriptionFormError(LAST_NAME_ERROR)
    class OnEtDniError : RegisterSubscriptionFormError(DNI_ERROR)
    class OnEtPasswordError : RegisterSubscriptionFormError(PASSWORD_ERROR)
    class OnEtAddressesError : RegisterSubscriptionFormError(ADDRESS_ERROR)
    class OnEtNumberPhoneError : RegisterSubscriptionFormError(NUMBER_PHONE_ERROR)
    class OnEtSubscriptionDateError : RegisterSubscriptionFormError(SUBSCRIPTION_DATE_ERROR)
    class OnSpnPlanError : RegisterSubscriptionFormError(SPN_PLAN_ERROR)
    class OnSpnNetworkDeviceError : RegisterSubscriptionFormError(SPN_NETWORK_DEVICE_ERROR)
    class OnSpnPlaceError : RegisterSubscriptionFormError(SPN_PLACE_ERROR)
    class OnSpnTechnicianError : RegisterSubscriptionFormError(SPN_TECHNICIAN_ERROR)
    class OnSpnNapBoxError : RegisterSubscriptionFormError(SPN_NAP_BOX_ERROR)
    class OnEtLocationError : RegisterSubscriptionFormError(LOCATION_ERROR)
    class OnDniIsInvalidError : RegisterSubscriptionFormError(DNI_IS_INVALID_ERROR)
    class OnPasswordIsInvalidError : RegisterSubscriptionFormError(PASSWORD_IS_INVALID_ERROR)
    class OnPhoneIsInvalidError : RegisterSubscriptionFormError(PHONE_IS_INVALID_ERROR)
    class OnEtFirstNameIsInvalidError : RegisterSubscriptionFormError(FIRST_NAME_IS_INVALID_ERROR)
    class OnEtLastNameIsInvalidError : RegisterSubscriptionFormError(LAST_NAME_IS_INVALID_ERROR)
    class HostDeviceError : RegisterSubscriptionFormError(SPN_NETWORK_DEVICE_ERROR)

}
