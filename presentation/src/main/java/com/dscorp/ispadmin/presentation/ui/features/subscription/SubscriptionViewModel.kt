package com.dscorp.ispadmin.presentation.ui.features.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState.EditSubscriptionError
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionUiState.EditSubscriptionSuccess
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.InstallationType
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.dscorp.ispadmin.presentation.util.Constants
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidPhone
import com.example.data2.data.repository.IOltRepository
import com.example.data2.data.repository.IRepository
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val repository: IRepository,
    private val oltRepository: IOltRepository
) : ViewModel() {
    val editSubscriptionUiState = MutableLiveData<EditSubscriptionUiState>()
    val editFormErrorLiveData = MutableLiveData<EditSubscriptionFormErrorUiState>()

    val registerSubscriptionUiState = MutableLiveData<RegisterSubscriptionUiState>()
    var subscription: SubscriptionResponse? = null


    var selectedLocation: LatLng? = null
        set(value) {
            field = value
            locationField.validateField(Gson().toJson(value))
        }

    var selectedPlan: Plan? = null
        set(value) {
            field = value
            planField.validateField(Gson().toJson(value))
        }

    var selectedPlace: Place? = null
        set(value) {
            field = value
            placeField.validateField(Gson().toJson(field))
        }

    var selectedTechnician: Technician? = null
        set(value) {
            field = value
            technicianField.validateField(Gson().toJson(field))
        }

    var selectedHostNetworkDevice: NetworkDevice? = null
        set(value) {
            field = value
            hostDeviceField.validateField(Gson().toJson(field))
        }

    var selectedDate: Long = 0
    set(value) {
        field =value
        subscriptionDateField.validateField(value.toString())
    }


    var selectedNetworkDeviceOne: NetworkDevice? = null
    var selectedNetworkDeviceTwo: NetworkDevice? = null
    var selectedNapBox: NapBox? = null
    var installationType: InstallationType? = null

    var selectedAdditionalDevice = MutableLiveData<NetworkDevice?>(null)


    var additionalNetworkDevicesList = mutableSetOf<NetworkDevice?>()

    val addButtonIsEnabled = Transformations.map(selectedAdditionalDevice) { it != null }

    private val cpeDevices = MutableStateFlow<List<NetworkDevice>?>(null)

    private val fiberCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.FIBER_ROUTER }
    }

    private val wirelessCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.WIRELESS_ROUTER }
    }

    val nameField = FormField(
        hintResourceId = R.string.name,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val lastNameField = FormField(
        hintResourceId = R.string.lastName,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val dniField = FormField(
        hintResourceId = R.string.dni,
        errorResourceId = R.string.invalidDNI,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean = fieldValue.isValidDni()
        }
    )

    val passwordField = FormField(
        hintResourceId = R.string.password,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val addressField = FormField(
        hintResourceId = R.string.address,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val phoneField = FormField(
        hintResourceId = R.string.phoneNumer,
        errorResourceId = R.string.invalidPhoneNumber,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                fieldValue.isValidPhone()
        }
    )

    val locationField = FormField(
        hintResourceId = R.string.location,
        errorResourceId = R.string.mustSelectLocation,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                Gson().fromJson(fieldValue, LatLng::class.java) != null

        }
    )

    val planField = FormField(
        hintResourceId = R.string.plan,
        errorResourceId = R.string.mustSelectPlan,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                Gson().fromJson(fieldValue, Plan::class.java) != null

        }
    )

    val placeField = FormField(
        hintResourceId = R.string.place,
        errorResourceId = R.string.mustSelectPlace,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                Gson().fromJson(fieldValue, Place::class.java) != null
        }
    )

    val technicianField = FormField(
        hintResourceId = R.string.technician,
        errorResourceId = R.string.mustSelectTechnician,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                Gson().fromJson(fieldValue, Technician::class.java) != null

        }
    )

    val hostDeviceField = FormField(
        hintResourceId = R.string.host_device,
        errorResourceId = R.string.mustSelectHostDevice,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean =
                Gson().fromJson(fieldValue, NetworkDevice::class.java) != null

        }
    )

    val subscriptionDateField = FormField(
        hintResourceId = R.string.subscriptionDate,
        errorResourceId = R.string.mustSelectSubscriptionDate,
        fieldValidator = object : FieldValidator {
            override fun checkIfFieldIsValid(fieldValue: String?): Boolean = !fieldValue.isNullOrEmpty()
        }
    )


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

        for (formField in listOf(
            nameField,
            lastNameField,
            dniField,
            passwordField,
            addressField,
            phoneField,
            locationField,
            planField,
            placeField,
            technicianField,
            hostDeviceField,
            subscriptionDateField
            )) {
            formField.isValid
        }

        return false
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
