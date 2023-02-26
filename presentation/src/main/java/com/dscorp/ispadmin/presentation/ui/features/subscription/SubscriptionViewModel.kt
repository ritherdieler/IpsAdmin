package com.dscorp.ispadmin.presentation.ui.features.subscription

import android.database.Observable
import android.view.View
import androidx.lifecycle.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.util.Constants
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IOltRepository
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    val repository: IRepository,
    val oltRepository: IOltRepository
) : ViewModel() {
    val editSubscriptionUiState = MutableLiveData<EditSubscriptionUiState>()
    val editFormErrorLiveData = MutableLiveData<EditSubscriptionFormErrorUiState>()

    val registerSubscriptionUiState = MutableLiveData<RegisterSubscriptionUiState>()
    val registerFormErrorLiveData = MutableLiveData<RegisterSubscriptionFormErrorUiState>()
    var subscription: SubscriptionResponse? = null

    var selectedAdditionalDevice = MutableLiveData<NetworkDevice?>(null)

    var additionalNetworkDevicesList = mutableSetOf<NetworkDevice>()

    val addButtonVisibility =
        Transformations.map(selectedAdditionalDevice) { if (it == null) View.GONE else View.VISIBLE }

    private val cpeDevices = MutableStateFlow<List<NetworkDevice>?>(null)


    // dependent flow on cpeDevices

    private val fiberCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.FIBER_ROUTER }
    }

    private val wirelessCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.WIRELESS_ROUTER }
    }


    init {
        viewModelScope.launch {
            val response = oltRepository.getUnconfirmedOnus()
            print(response)
        }
    }

    fun getFormData() = viewModelScope.launch {
        try {
            val cpeDevicesJob = async { repository.getCpeDevices() }
            val cpeDevicesList = cpeDevicesJob.await()
            cpeDevices.value = cpeDevicesList

            val genericDevicesJob = async { repository.getGenericDevices() }
            val plansJob = async { repository.getPlans() }
            val placeJob = async { repository.getPlaces() }
            val napBoxesJob = async { repository.getNapBoxes() }
            val deferredTechnicians = async { repository.getTechnicians() }
            val coreDevicesJob = async { repository.getCoreDevices() }
            val genericDevices = genericDevicesJob.await()
            val plans = plansJob.await()
            val places = placeJob.await()
            val technicians = deferredTechnicians.await()
            val napBoxes = napBoxesJob.await()
            val coreDevices = coreDevicesJob.await()

            registerSubscriptionUiState.postValue(
                RegisterSubscriptionUiState.FormDataFound(
                    plans, genericDevices, places,
                    technicians, napBoxes, coreDevices
                )
            )


        } catch (e: Exception) {
            e.printStackTrace()
            registerSubscriptionUiState.postValue(FormDataError(e.message.toString()))
        }
    }

    fun getFiberDevices() = viewModelScope.launch {
        fiberCpeDevices.collectLatest {
            registerSubscriptionUiState.postValue(it?.let { FiberDevicesFound(it) })
        }
    }

    fun getWirelessDevices() = viewModelScope.launch {
        wirelessCpeDevices.collectLatest {
            registerSubscriptionUiState.postValue(it?.let { WirelessDevicesFound(it) })

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
            if (!editFormIsValid(subscription)) return@launch
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

        if (subscription.lastName.isEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnEtLastNameErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtLastNameHasNotErrors
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
        if (subscription.technicianId == 0 || subscription.technicianId == null) {
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

        if (subscription.firstName.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnEtFirstNameErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtFirstNameHasNotErrors
        }

        if (subscription.lastName.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnEtLastNameErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtLastNameHasNotErrors
        }

        if (subscription.password.isEmpty() || subscription.password.length < 8) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnEtPasswordErrorRegisterUiState()
            return false
        }

        if (subscription.dni.isEmpty()) {
            editSubscriptionUiState.value = EditSubscriptionError(Constants.GENERIC_ERROR)
            return false
        }
        if (subscription.technicianId == 0 || subscription.technicianId == null) {
            editSubscriptionUiState.value = EditSubscriptionError(Constants.GENERIC_ERROR)
            return false
        }
        if (subscription.address.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnEtAddressesErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtAddressHasNotErrors
        }

        if (subscription.location == null) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnEtLocationErrorRegisterUiState()
            return false
        }

        if (subscription.phone.isEmpty() || subscription.phone.length < 9) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnEtPhoneErrorRegisterUiState()
            return false
        }

        if (subscription.planId.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtPlanNotErrors
        }

        if (subscription.networkDeviceIds.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnNetworkDeviceErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtPlaceNotErrors
        }

        if (subscription.napBoxId.isEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnNapBoxErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtNapBoxNotErrors
        }

        if (subscription.hostDeviceId == 0) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.HostDeviceError()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtNapBoxNotErrors
        }
        return true
    }

    fun addSelectedAdditionalNetworkDeviceToList() {
        selectedAdditionalDevice.value?.let {
            additionalNetworkDevicesList.add(it)
        }
    }

    fun resetAdditionalDevicesValues() {
        selectedAdditionalDevice.value = null
        additionalNetworkDevicesList = mutableSetOf()
    }
}
