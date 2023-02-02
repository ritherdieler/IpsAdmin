package com.dscorp.ispadmin.presentation.ui.features.subscription.register

sealed class RegisterSubscriptionCleanForm {

    object OnEtLastNameHasNotErrors : RegisterSubscriptionCleanForm()
    object OnEtFirstNameHasNotErrors : RegisterSubscriptionCleanForm()
    object OnEtDniHasNotErrors : RegisterSubscriptionCleanForm()
    object OnEtPasswordHasNotErrors : RegisterSubscriptionCleanForm()
    object OnEtAddressHasNotErrors : RegisterSubscriptionCleanForm()
    object OnEtPhoneHasNotErrors : RegisterSubscriptionCleanForm()
    object OnEtSubscriptionDateNotErrors : RegisterSubscriptionCleanForm()
    object OnEtPlanNotErrors : RegisterSubscriptionCleanForm()
    object OnEtNetworkDeviceNotErrors : RegisterSubscriptionCleanForm()
    object OnEtPlaceNotErrors : RegisterSubscriptionCleanForm()
    object OnEtTechnicianNotErrors : RegisterSubscriptionCleanForm()
    object OnEtNapBoxNotErrors : RegisterSubscriptionCleanForm()

/*    object Idle : RegisterSubscriptionCleanForm()*/
}
