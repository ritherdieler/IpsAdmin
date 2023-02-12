package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.extension.hasOnlyLetters
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RegisterSubscriptionViewModel(val repository: IRepository) : ViewModel() {
    val uiState = MutableLiveData<RegisterSubscriptionUiState>()
    val formErrorLiveData = MutableLiveData<RegisterSubscriptionFormError>()
    val cleanFormLiveData = MutableLiveData<RegisterSubscriptionCleanForm>()

    init {
        getFormData()
    }

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

            uiState.postValue(
                FormDataFound(
                    plansFromRepository, devicesFromRepository, placeFromRepository,
                    technicians, napBoxesFromRepository,coreDevices
                )
            )

        } catch (e: Exception) {
            uiState.postValue(FormDataError(e.message.toString()))
        }
    }

    fun registerSubscription(subscription: Subscription) = viewModelScope.launch {
        try {
            if (formIsValid(subscription)) {
                val subscriptionFromRepository = repository.doSubscription(subscription)
                uiState.postValue(
                    RegisterSubscriptionRegistered(
                        subscriptionFromRepository
                    )
                )
            }

        } catch (error: Exception) {
            uiState.postValue(RegisterSubscriptionError(error.message.toString()))
        }
    }

    private fun formIsValid(subscription: Subscription): Boolean {

        if (subscription.firstName.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtFirstNameError()
            return false

        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtFirstNameHasNotErrors

        }
        if (subscription.firstName.hasOnlyLetters()) {
            formErrorLiveData.value = RegisterSubscriptionFormError.OnEtFirstNameIsInvalidError()
            return false
        }

        if (subscription.lastName.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtLastNameError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtLastNameHasNotErrors
        }
        if (subscription.lastName.hasOnlyLetters()) {
            formErrorLiveData.value = RegisterSubscriptionFormError.OnEtLastNameIsInvalidError()
            return false
        }

        if (subscription.dni.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtDniError()
            return false
        }
        if (subscription.dni.length < 8) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnDniIsInvalidError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtDniHasNotErrors
        }
        if (subscription.password.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtPasswordError()
            return false
        }
        if (subscription.password.length < 8) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnPasswordIsInvalidError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPasswordHasNotErrors
        }

        if (subscription.address.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtAddressesError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtAddressHasNotErrors
        }

        if (subscription.location == null) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtLocationError()
            return false
        }

        if (subscription.phone.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtNumberPhoneError()
            return false
        }

        if (subscription.phone.length < 9) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnPhoneIsInvalidError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPhoneHasNotErrors
        }

        if (subscription.subscriptionDate == 0L) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnEtSubscriptionDateError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtSubscriptionDateNotErrors
        }

        if (subscription.planId.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnSpnPlanError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPlanNotErrors
        }

        if (subscription.networkDeviceIds.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnSpnNetworkDeviceError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnSpnPlaceError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtPlaceNotErrors
        }
        if (subscription.technicianId.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnSpnTechnicianError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtTechnicianNotErrors
        }

        if (subscription.napBoxId.isEmpty()) {
            formErrorLiveData.value =
                RegisterSubscriptionFormError.OnSpnNapBoxError()
            return false
        } else {
            cleanFormLiveData.value = RegisterSubscriptionCleanForm.OnEtNapBoxNotErrors
        }
        return true
    }




}






