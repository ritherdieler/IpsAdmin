package com.dscorp.ispadmin.presentation.subscription

import com.dscorp.ispadmin.presentation.place.FormError

/**
 * Created by Sergio Carrillo Diestra on 13/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class
SubscriptionFormError {
    object OnFirstNameHasNotErrors : SubscriptionFormError()
    class OnEtFirstNameError(val error: String) : SubscriptionFormError()
    class OnEtLastNameError(val error: String) : SubscriptionFormError()
    class OnEtDniError(val error: String) : SubscriptionFormError()
    class OnEtPasswordError(val error: String) : SubscriptionFormError()
    class OnEtAddressesError(val error: String) : SubscriptionFormError()
    class OnEtNumberPhoneError(val error: String) : SubscriptionFormError()
    class OnEtSubscriptionDateError(val error: String) : SubscriptionFormError()
    class OnSpnPlanError(val error: String) : SubscriptionFormError()
    class OnSpnNetworkDeviceError(val error: String) : SubscriptionFormError()
    class OnSpnPlaceError(val error: String) : SubscriptionFormError()
    class OnEtLocationError(val error: String) : SubscriptionFormError()


}
