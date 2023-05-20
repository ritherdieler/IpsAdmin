package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.forms.SubscriptionForm
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.google.android.gms.maps.model.LatLng

class SubscriptionDetailViewModel(val subscriptionForm: SubscriptionForm) :
    BaseViewModel<SubscriptionDetailUiState>() {

    fun initForm(subscription: SubscriptionResponse) {
        subscriptionForm.firstNameField.setValue(subscription.firstName)
        subscriptionForm.lastNameField.setValue(subscription.lastName)
        subscriptionForm.dniField.setValue(subscription.dni)
        subscriptionForm.addressField.setValue(subscription.address)
        subscriptionForm.ipField.setValue(subscription.ip)
        subscriptionForm.locationField.setValue(subscription.location?.let {
            LatLng(
                it.latitude,
                it.longitude
            )
        })

        subscriptionForm.phoneField.setValue(subscription.phone)
        subscriptionForm.planField.setValue(subscription.plan)
        subscriptionForm.placeField.setValue(subscription.place)
        subscriptionForm.technicianField.setValue(subscription.technician)
        subscriptionForm.hostDeviceField.setValue(subscription.hostDevice)
        subscriptionForm.subscriptionDateField.setValue(subscription.subscriptionDate)
        subscriptionForm.isMigrationField.setValue(subscription.isMigration)
        subscriptionForm.priceField.setValue(subscription.price.toString())
        subscriptionForm.noteField.setValue(subscription.note)


    }

    fun makeFieldsEditable() {
        with(subscriptionForm) {
            ipField.isEditableLiveData.value = false
            technicianField.isEditableLiveData.value = false
            subscriptionDateField.isEditableLiveData.value = false
            priceField.isEditableLiveData.value = false
            isMigrationField.isEditableLiveData.value = false
        }
    }


}

sealed class SubscriptionDetailUiState {
    object SubscriptionUpdated : SubscriptionDetailUiState()

}
