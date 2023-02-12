package com.dscorp.ispadmin.presentation.ui.features.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionCleanForm
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidNameOrLastName
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SubscriptionViewModel(val repository: IRepository) : ViewModel() {
    val registerSubscriptionUiState = MutableLiveData<RegisterSubscriptionUiState>()
    val editSubscriptionUiState = MutableLiveData<EditSubscriptionUiState>()

    val formErrorLiveData = MutableLiveData<SubscriptionFormError>()
    val cleanFormLiveData = MutableLiveData<RegisterSubscriptionCleanForm>()
    var subscription: SubscriptionResponse? = null

    fun getFormData() = viewModelScope.launch {
        try {
            val devicesJob = async { repository.getDevices() }
            val plansJob = async { repository.getPlans() }
            val placeJob = async { repository.getPlaces() }
            val napBoxesJob = async { repository.getNapBoxes() }
            val deferredTechnicians = async { repository.getTechnicians() }
            val coreDevicesJob = async { repository.getCoreDevices() }
            val devicesFromRepository = devicesJob.await()
            val plansFromRepository = plansJob.await()
            val placeFromRepository = placeJob.await()
            val technicians = deferredTechnicians.await()
            val napBoxesFromRepository = napBoxesJob.await()
            val coreDevices = coreDevicesJob.await()

            registerSubscriptionUiState.postValue(
                FormDataFound(
                    plansFromRepository, devicesFromRepository, placeFromRepository,
                    technicians, napBoxesFromRepository, coreDevices
                )
            )

        } catch (e: Exception) {
            registerSubscriptionUiState.postValue(FormDataError(e.message.toString()))
        }
    }

    fun registerSubscription(subscription: Subscription) = viewModelScope.launch {
        try {
            if (!formIsValid(subscription)) return@launch
            val subscriptionFromRepository = repository.doSubscription(subscription)
            registerSubscriptionUiState.postValue(
                RegisterSubscriptionSuccess(subscriptionFromRepository)
            )
        } catch (error: Exception) {
            registerSubscriptionUiState.postValue(RegisterSubscriptionError(error.message.toString()))
        }
    }


    fun editSubscription(subscription: Subscription) = viewModelScope.launch {
        try {
            if (!formIsValid(subscription)) return@launch
            val response = repository.editSubscription(subscription)
            editSubscriptionUiState.postValue(EditSubscriptionSuccess(response))
        } catch (e: Exception) {
            editSubscriptionUiState.postValue(EditSubscriptionError(e.message))
        }
    }

    private fun formIsValid(subscription: Subscription): Boolean {

        if (subscription.firstName.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtFirstNameError()
            return false

        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtFirstNameHasNotErrors

        }
        if (!subscription.firstName.isValidNameOrLastName()) {
            formErrorLiveData.value = SubscriptionFormError.OnEtFirstNameIsInvalidError()
            return false
        }

        if (subscription.lastName.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtLastNameError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtLastNameHasNotErrors
        }

        if (!subscription.lastName.isValidNameOrLastName()) {
            formErrorLiveData.value = SubscriptionFormError.OnEtLastNameIsInvalidError()
            return false
        }

        if (subscription.dni.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtDniError()
            return false
        }

        if (subscription.dni.length < 8) {
            formErrorLiveData.value =
                SubscriptionFormError.OnDniIsInvalidError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtDniHasNotErrors
        }

        if (subscription.password.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtPasswordError()
            return false
        }

        if (subscription.password.length < 8) {
            formErrorLiveData.value =
                SubscriptionFormError.OnPasswordIsInvalidError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPasswordHasNotErrors
        }

        if (subscription.address.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtAddressesError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtAddressHasNotErrors
        }

        if (subscription.location == null) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtLocationError()
            return false
        }

        if (subscription.phone.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtNumberPhoneError()
            return false
        }

        if (subscription.phone.length < 9) {
            formErrorLiveData.value =
                SubscriptionFormError.OnPhoneIsInvalidError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPhoneHasNotErrors
        }

        if (subscription.subscriptionDate == 0L) {
            formErrorLiveData.value =
                SubscriptionFormError.OnEtSubscriptionDateError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtSubscriptionDateNotErrors
        }

        if (subscription.planId.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnSpnPlanError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPlanNotErrors
        }

        if (subscription.networkDeviceIds.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnSpnNetworkDeviceError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnSpnPlaceError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPlaceNotErrors
        }
        if (subscription.technicianId.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnSpnTechnicianError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtTechnicianNotErrors
        }

        if (subscription.napBoxId.isEmpty()) {
            formErrorLiveData.value =
                SubscriptionFormError.OnSpnNapBoxError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtNapBoxNotErrors
        }

        if (subscription.hostDeviceId == 0) {
            formErrorLiveData.value =
                SubscriptionFormError.HostDeviceError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtNapBoxNotErrors
        }
        return true
    }

}






