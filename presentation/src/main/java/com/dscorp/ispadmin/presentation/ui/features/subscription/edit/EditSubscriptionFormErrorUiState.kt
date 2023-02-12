package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class EditSubscriptionFormErrorUiState(val error:String? = null) {
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
    class OnEtPasswordErrorRegisterUiState : EditSubscriptionFormErrorUiState(PASSWORD_ERROR)
    class OnEtAddressesErrorRegisterUiState : EditSubscriptionFormErrorUiState(ADDRESS_ERROR)
    class OnEtNumberPhoneErrorRegisterUiState : EditSubscriptionFormErrorUiState(NUMBER_PHONE_ERROR)
    class OnSpnPlanErrorRegisterUiState : EditSubscriptionFormErrorUiState(SPN_PLAN_ERROR)
    class OnSpnNetworkDeviceErrorRegisterUiState : EditSubscriptionFormErrorUiState(SPN_NETWORK_DEVICE_ERROR)
    class OnSpnPlaceErrorRegisterUiState : EditSubscriptionFormErrorUiState(SPN_PLACE_ERROR)
    class OnSpnNapBoxErrorRegisterUiState : EditSubscriptionFormErrorUiState(SPN_NAP_BOX_ERROR)
    class OnEtLocationErrorRegisterUiState : EditSubscriptionFormErrorUiState(LOCATION_ERROR)
    class OnPasswordIsInvalidErrorRegisterUiState : EditSubscriptionFormErrorUiState(PASSWORD_IS_INVALID_ERROR)
    class OnPhoneIsInvalidErrorRegisterUiState : EditSubscriptionFormErrorUiState(PHONE_IS_INVALID_ERROR)
    class OnEtFirstNameIsInvalidErrorRegisterUiState : EditSubscriptionFormErrorUiState(FIRST_NAME_IS_INVALID_ERROR)
    class OnEtLastNameIsInvalidErrorRegisterUiState : EditSubscriptionFormErrorUiState(LAST_NAME_IS_INVALID_ERROR)
    class HostUiStateDeviceErrorRegister : EditSubscriptionFormErrorUiState(SPN_NETWORK_DEVICE_ERROR)

    object CleanEtLastNameHasNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtFirstNameHasNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtPasswordHasNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtAddressHasNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtPhoneHasNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtPlanNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtNetworkDeviceNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtPlaceNotErrors : EditSubscriptionFormErrorUiState()
    object CleanEtNapBoxNotErrors : EditSubscriptionFormErrorUiState()


}
