package com.dscorp.ispadmin.presentation.ui.features.subscription

import androidx.lifecycle.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.InstallationType
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.NameField
import com.dscorp.ispadmin.presentation.util.Constants
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.repository.IOltRepository
import com.example.data2.data.repository.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val repository: IRepository,
    private val oltRepository: IOltRepository
) : ViewModel() {
    val editSubscriptionUiState = MutableLiveData<EditSubscriptionUiState>()
    val editFormErrorLiveData = MutableLiveData<EditSubscriptionFormErrorUiState>()

    val registerSubscriptionUiState = MutableLiveData<RegisterSubscriptionUiState>()
    val registerFormErrorLiveData = MutableLiveData<RegisterSubscriptionFormErrorUiState>()
    var subscription: SubscriptionResponse? = null
    var selectedDate: Long = 0
    var selectedLocation: LatLng? = null
    var selectedPlan: Plan? = null
    var selectedNetworkDeviceOne: NetworkDevice? = null
    var selectedNetworkDeviceTwo: NetworkDevice? = null
    var selectedHostNetworkDevice: NetworkDevice? = null
    var selectedTechnician: Technician? = null
    var selectedPlace: Place? = null
    var selectedNapBox: NapBox? = null
    var installationType: InstallationType? = null

    var selectedAdditionalDevice = MutableLiveData<NetworkDevice?>(null)

    var additionalNetworkDevicesList = mutableSetOf<NetworkDevice>()

    val addButtonIsEnabled = Transformations.map(selectedAdditionalDevice) { it != null }

    private val cpeDevices = MutableStateFlow<List<NetworkDevice>?>(null)

    val nameField = NameField()

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
            subscription.apply {
                planId = selectedPlan?.id ?: ""
                placeId = selectedPlace?.id ?: ""
                location = GeoLocation(
                    selectedLocation?.latitude ?: 0.0,
                    selectedLocation?.longitude ?: 0.0
                )
                technicianId = selectedTechnician?.id
                napBoxId = selectedNapBox?.id ?: ""
                subscriptionDate = selectedDate
                hostDeviceId = selectedHostNetworkDevice?.id ?: 0

                val installedDevices = mutableListOf<Int>()
                selectedNetworkDeviceOne?.let { it.id?.let { it1 -> installedDevices.add(it1) } }
                selectedNetworkDeviceTwo?.let { it.id?.let { it1 -> installedDevices.add(it1) } }
                additionalDevices = installedDevices
            }

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

        if (!nameField.isValid) return false

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

        if (subscription.planId.isNullOrEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtPlanNotErrors
        }

        if (subscription.additionalDevices.isNullOrEmpty()) {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.OnSpnNetworkDeviceErrorRegisterUiState()
            return false
        } else {
            registerFormErrorLiveData.value =
                RegisterSubscriptionFormErrorUiState.CleanEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isNullOrEmpty()) {
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

        if (subscription.napBoxId.isNullOrEmpty()) {
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

        if (subscription.planId.isNullOrEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtPlanNotErrors
        }

        if (subscription.additionalDevices.isNullOrEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnNetworkDeviceErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtNetworkDeviceNotErrors
        }

        if (subscription.placeId.isNullOrEmpty()) {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.OnSpnPlanErrorRegisterUiState()
            return false
        } else {
            editFormErrorLiveData.value =
                EditSubscriptionFormErrorUiState.CleanEtPlaceNotErrors
        }

        if (subscription.napBoxId.isNullOrEmpty()) {
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
