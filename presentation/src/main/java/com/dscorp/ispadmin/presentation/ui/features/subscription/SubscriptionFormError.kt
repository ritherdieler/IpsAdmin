package com.dscorp.ispadmin.presentation.ui.features.subscription

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class SubscriptionFormError(val error:String) {
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
    class OnEtFirstNameError : SubscriptionFormError(FIRST_NAME_ERROR)
    class OnEtLastNameError : SubscriptionFormError(LAST_NAME_ERROR)
    class OnEtDniError : SubscriptionFormError(DNI_ERROR)
    class OnEtPasswordError : SubscriptionFormError(PASSWORD_ERROR)
    class OnEtAddressesError : SubscriptionFormError(ADDRESS_ERROR)
    class OnEtNumberPhoneError : SubscriptionFormError(NUMBER_PHONE_ERROR)
    class OnEtSubscriptionDateError : SubscriptionFormError(SUBSCRIPTION_DATE_ERROR)
    class OnSpnPlanError : SubscriptionFormError(SPN_PLAN_ERROR)
    class OnSpnNetworkDeviceError : SubscriptionFormError(SPN_NETWORK_DEVICE_ERROR)
    class OnSpnPlaceError : SubscriptionFormError(SPN_PLACE_ERROR)
    class OnSpnTechnicianError : SubscriptionFormError(SPN_TECHNICIAN_ERROR)
    class OnSpnNapBoxError : SubscriptionFormError(SPN_NAP_BOX_ERROR)
    class OnEtLocationError : SubscriptionFormError(LOCATION_ERROR)
    class OnDniIsInvalidError : SubscriptionFormError(DNI_IS_INVALID_ERROR)
    class OnPasswordIsInvalidError : SubscriptionFormError(PASSWORD_IS_INVALID_ERROR)
    class OnPhoneIsInvalidError : SubscriptionFormError(PHONE_IS_INVALID_ERROR)
    class OnEtFirstNameIsInvalidError : SubscriptionFormError(FIRST_NAME_IS_INVALID_ERROR)
    class OnEtLastNameIsInvalidError : SubscriptionFormError(LAST_NAME_IS_INVALID_ERROR)
    class HostDeviceError : SubscriptionFormError(SPN_NETWORK_DEVICE_ERROR)

}
