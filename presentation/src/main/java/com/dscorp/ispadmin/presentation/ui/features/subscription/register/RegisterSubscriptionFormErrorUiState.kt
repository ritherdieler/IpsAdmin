package com.dscorp.ispadmin.presentation.ui.features.subscription.register

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionFormErrorUiState(val error: String? = null) {
    companion object {
        const val FIRST_NAME_ERROR = "Este campo no puede estar vacio"
        const val LAST_NAME_ERROR = "Este campo no puede estar vacio"
        const val DNI_ERROR = "Este campo no puede estar vacio"
        const val PASSWORD_ERROR = "Este campo no puede estar vacio"
        const val ADDRESS_ERROR = "Este campo no puede estar vacio"
        const val NUMBER_PHONE_ERROR = "Este campo no puede estar vacio"
        const val SUBSCRIPTION_DATE_ERROR = "Este campo no puede estar vacio"
        const val SPN_PLAN_ERROR = "Este campo no puede estar vacio"
        const val SPN_NETWORK_DEVICE_ERROR = "Este campo no puede estar vacio"
        const val SPN_PLACE_ERROR = "Este campo no puede estar vacio"
        const val SPN_TECHNICIAN_ERROR = "Este campo no puede estar vacio"
        const val SPN_NAP_BOX_ERROR = "Este campo no puede estar vacio"
        const val LOCATION_ERROR = "Este campo no puede estar vacio"
        const val DNI_IS_INVALID_ERROR = "DNI requiere 8 digitos"
        const val PASSWORD_IS_INVALID_ERROR = "Requiere entre 8 a 20 caracteres"
        const val PHONE_IS_INVALID_ERROR = "Requiere 9 digitos"
        const val FIRST_NAME_IS_INVALID_ERROR = "Este nombre no es valido"
        const val LAST_NAME_IS_INVALID_ERROR = "Este nombre no es valido"
    }
    class OnEtFirstNameErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(FIRST_NAME_ERROR)
    class OnEtLastNameErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(LAST_NAME_ERROR)
    class OnEtDniErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(DNI_ERROR)
    class OnEtPasswordErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(PASSWORD_ERROR)
    class OnEtAddressesErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(ADDRESS_ERROR)
    class OnEtNumberPhoneErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(NUMBER_PHONE_ERROR)
    class OnEtRegisterSubscriptionDateErrorUiState : RegisterSubscriptionFormErrorUiState(SUBSCRIPTION_DATE_ERROR)
    class OnSpnPlanErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(SPN_PLAN_ERROR)
    class OnSpnNetworkDeviceErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(SPN_NETWORK_DEVICE_ERROR)
    class OnSpnPlaceErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(SPN_PLACE_ERROR)
    class OnSpnTechnicianErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(SPN_TECHNICIAN_ERROR)
    class OnSpnNapBoxErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(SPN_NAP_BOX_ERROR)
    class OnEtLocationErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(LOCATION_ERROR)
    class OnDniIsInvalidErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(DNI_IS_INVALID_ERROR)
    class OnPasswordIsInvalidErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(PASSWORD_IS_INVALID_ERROR)
    class OnPhoneIsInvalidErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(PHONE_IS_INVALID_ERROR)
    class OnEtFirstNameIsInvalidErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(FIRST_NAME_IS_INVALID_ERROR)
    class OnEtLastNameIsInvalidErrorRegisterUiState : RegisterSubscriptionFormErrorUiState(LAST_NAME_IS_INVALID_ERROR)
    class HostUiStateDeviceErrorRegister : RegisterSubscriptionFormErrorUiState(SPN_NETWORK_DEVICE_ERROR)

    object CleanEtLastNameHasNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtFirstNameHasNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtDniHasNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtPasswordHasNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtAddressHasNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtPhoneHasNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtSubscriptionDateNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtPlanNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtNetworkDeviceNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtPlaceNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtTechnicianNotErrors : RegisterSubscriptionFormErrorUiState()
    object CleanEtNapBoxNotErrors : RegisterSubscriptionFormErrorUiState()
}
