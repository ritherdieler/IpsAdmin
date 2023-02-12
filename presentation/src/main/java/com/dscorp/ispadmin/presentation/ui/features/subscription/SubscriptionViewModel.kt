package com.dscorp.ispadmin.presentation.ui.features.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState
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

    val registerFormErrorLiveData = MutableLiveData<RegisterSubscriptionFormErrorUiState>()
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
                RegisterSubscriptionUiState.FormDataFound(
                    plansFromRepository, devicesFromRepository, placeFromRepository,
                    technicians, napBoxesFromRepository, coreDevices
                )
            )

            editSubscriptionUiState.postValue(
                EditSubscriptionUiState.FormDataFound(
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
            if (!registrationFormIsValid(subscription)) return@launch
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
            if (!registrationFormIsValid(subscription)) return@launch
            val response = repository.editSubscription(subscription)
            editSubscriptionUiState.postValue(EditSubscriptionSuccess(response))
        } catch (e: Exception) {
            editSubscriptionUiState.postValue(EditSubscriptionError(e.message))
        }
    }

    private fun registrationFormIsValid(subscription: Subscription): Boolean {

        if (subscription.firstName.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtFirstNameErrorRegisterUiState()
            return false

        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtFirstNameHasNotErrors

        }
        if (!subscription.firstName.isValidNameOrLastName()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtFirstNameIsInvalidErrorRegisterUiState()
            return false
        }

        if (subscription.lastName.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtLastNameErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtLastNameHasNotErrors
        }

        if (!subscription.lastName.isValidNameOrLastName()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtLastNameIsInvalidErrorRegisterUiState()
            return false
        }

        if (subscription.dni.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtDniErrorRegisterUiState()
            return false
        }


        if (subscription.password.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtPasswordErrorRegisterUiState()
            return false
        }

        if (subscription.password.length < 8) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnPasswordIsInvalidErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPasswordHasNotErrors
        }

        if (subscription.address.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtAddressesErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtAddressHasNotErrors
        }

        if (subscription.location == null) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtLocationErrorRegisterUiState()
            return false
        }

        if (subscription.phone.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtNumberPhoneErrorRegisterUiState()
            return false
        }

        if (subscription.phone.length < 9) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnPhoneIsInvalidErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPhoneHasNotErrors
        }

        if (subscription.subscriptionDate == 0L) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtRegisterSubscriptionDateErrorUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtSubscriptionDateNotErrors
        }

        if (subscription.planId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPlanNotErrors
        }

        if (subscription.networkDeviceIds.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnNetworkDeviceErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnPlaceErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPlaceNotErrors
        }
        if (subscription.technicianId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnTechnicianErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtTechnicianNotErrors
        }

        if (subscription.napBoxId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnNapBoxErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNapBoxNotErrors
        }

        if (subscription.hostDeviceId == 0) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.HostUiStateDeviceErrorRegister()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNapBoxNotErrors
        }
        return true
    }

    private fun editFormIsValid(subscription: Subscription): Boolean {

        if (subscription.password.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtPasswordErrorRegisterUiState()
            return false
        }

        if (subscription.password.length < 8) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnPasswordIsInvalidErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPasswordHasNotErrors
        }

        if (subscription.address.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtAddressesErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtAddressHasNotErrors
        }

        if (subscription.location == null) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtLocationErrorRegisterUiState()
            return false
        }

        if (subscription.phone.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtNumberPhoneErrorRegisterUiState()
            return false
        }

        if (subscription.phone.length < 9) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnPhoneIsInvalidErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPhoneHasNotErrors
        }

        if (subscription.planId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPlanNotErrors
        }

        if (subscription.networkDeviceIds.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnNetworkDeviceErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnPlaceErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPlaceNotErrors
        }

        if (subscription.napBoxId.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnNapBoxErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNapBoxNotErrors
        }

        if (subscription.hostDeviceId == 0) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.HostUiStateDeviceErrorRegister()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNapBoxNotErrors
        }
        return true
    }

}






