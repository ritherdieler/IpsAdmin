package com.dscorp.ispadmin.presentation.ui.features.subscription.register

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterSubscriptionFormError {

    class OnEtFirstNameError(val error: String) : RegisterSubscriptionFormError()
    class OnEtLastNameError(val error: String) : RegisterSubscriptionFormError()
    class OnEtDniError(val error: String) : RegisterSubscriptionFormError()
    class OnEtPasswordError(val error: String) : RegisterSubscriptionFormError()
    class OnEtAddressesError(val error: String) : RegisterSubscriptionFormError()
    class OnEtNumberPhoneError(val error: String) : RegisterSubscriptionFormError()
    class OnEtSubscriptionDateError(val error: String) : RegisterSubscriptionFormError()
    class OnSpnPlanError(val error: String) : RegisterSubscriptionFormError()
    class OnSpnNetworkDeviceError(val error: String) : RegisterSubscriptionFormError()
    class OnSpnPlaceError(val error: String) : RegisterSubscriptionFormError()
    class OnSpnTechnicianError(val error: String) : RegisterSubscriptionFormError()
    class OnSpnNapBoxError(val error: String) : RegisterSubscriptionFormError()
    class OnEtLocationError(val error: String) : RegisterSubscriptionFormError()

    class OnDniIsInvalidError(val error: String) : RegisterSubscriptionFormError()
    class OnPasswordIsInvalidError(val error: String) : RegisterSubscriptionFormError()
    class OnPhoneIsInvalidError(val error: String) : RegisterSubscriptionFormError()


}
