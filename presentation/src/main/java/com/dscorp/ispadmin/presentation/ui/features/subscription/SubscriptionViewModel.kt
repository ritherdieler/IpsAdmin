package com.dscorp.ispadmin.presentation.ui.features.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.dashboard.DashBoardDataUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidPhone
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val repository: IRepository,
) : ViewModel() {
    val registerSubscriptionUiState = MutableSharedFlow<RegisterSubscriptionUiState>()
    var subscription: SubscriptionResponse? = null

    var installationType: InstallationType? = null

    var selectedAdditionalDevice = MutableLiveData<NetworkDevice?>(null)

    val addButtonIsEnabled = Transformations.map(selectedAdditionalDevice) { it != null }

    var additionalNetworkDevicesList = mutableSetOf<NetworkDevice>()

    private val cpeDevices = MutableStateFlow<List<NetworkDevice>?>(null)

    private val fiberCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.FIBER_ROUTER }
    }

    private val wirelessCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.WIRELESS_ROUTER }
    }

    val firstNameField = FormField(
        hintResourceId = R.string.name,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator<String> {
            override fun validate(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val lastNameField = FormField(
        hintResourceId = R.string.lastName,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator<String> {
            override fun validate(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val dniField = FormField(
        hintResourceId = R.string.dni,
        errorResourceId = R.string.invalidDNI,
        fieldValidator = object : FieldValidator<String> {
            override fun validate(fieldValue: String?): Boolean = fieldValue.isValidDni()
        }
    )

    val addressField = FormField(
        hintResourceId = R.string.address,
        errorResourceId = R.string.fieldMustNotBeEmpty,
        fieldValidator = object : FieldValidator<String> {
            override fun validate(fieldValue: String?): Boolean =
                !fieldValue.isNullOrEmpty()
        }
    )

    val phoneField = FormField(
        hintResourceId = R.string.phoneNumer,
        errorResourceId = R.string.invalidPhoneNumber,
        fieldValidator = object : FieldValidator<String?> {
            override fun validate(fieldValue: String?): Boolean =
                fieldValue.isValidPhone()
        }
    )

    val locationField = FormField(
        hintResourceId = R.string.location,
        errorResourceId = R.string.mustSelectLocation,
        fieldValidator = object : FieldValidator<LatLng?> {
            override fun validate(fieldValue: LatLng?): Boolean =
                fieldValue != null
        }
    )

    val planField = FormField(
        hintResourceId = R.string.plan,
        errorResourceId = R.string.mustSelectPlan,
        fieldValidator = object : FieldValidator<PlanResponse?> {
            override fun validate(fieldValue: PlanResponse?): Boolean = fieldValue != null
        }
    )

    val placeField = FormField(
        hintResourceId = R.string.place,
        errorResourceId = R.string.mustSelectPlace,
        fieldValidator = object : FieldValidator<PlaceResponse> {
            override fun validate(fieldValue: PlaceResponse?): Boolean =
                fieldValue != null
        }
    )

    val technicianField = FormField(
        hintResourceId = R.string.technician,
        errorResourceId = R.string.mustSelectTechnician,
        fieldValidator = object : FieldValidator<Technician> {
            override fun validate(fieldValue: Technician?): Boolean =
                fieldValue != null
        }
    )

    val hostDeviceField = FormField(
        hintResourceId = R.string.host_device,
        errorResourceId = R.string.mustSelectHostDevice,
        fieldValidator = object : FieldValidator<NetworkDevice> {
            override fun validate(fieldValue: NetworkDevice?): Boolean =
                fieldValue != null
        }
    )

    val subscriptionDateField = FormField(
        hintResourceId = R.string.subscriptionDate,
        errorResourceId = R.string.mustSelectSubscriptionDate,
        fieldValidator = object : FieldValidator<Long> {
            override fun validate(fieldValue: Long?): Boolean = fieldValue != null
        }
    )

    val cpeDeviceField = FormField(
        hintResourceId = R.string.select_cpe_device,
        errorResourceId = R.string.mustSelectCpeDevice,
        fieldValidator = object : FieldValidator<NetworkDevice> {
            override fun validate(fieldValue: NetworkDevice?): Boolean = fieldValue != null
        }
    )

    val napBoxField = FormField(
        hintResourceId = R.string.selec_nap_box,
        errorResourceId = R.string.mustSelectNapBox,
        fieldValidator = object : FieldValidator<NapBoxResponse> {
            override fun validate(fieldValue: NapBoxResponse?): Boolean = fieldValue != null
        }
    )

    val onuField = FormField(
        hintResourceId = R.string.select_onu,
        errorResourceId = R.string.mustSelectOnu,
        fieldValidator = object : FieldValidator<Onu> {
            override fun validate(fieldValue: Onu?): Boolean = fieldValue != null
        }
    )


    val additionalDevicesField = FormField(
        hintResourceId = R.string.additionalDevices,
        errorResourceId = R.string.youCanSelectAdditionalNetworkDevices,
        fieldValidator = object : FieldValidator<List<NetworkDevice>> {
            override fun validate(fieldValue: List<NetworkDevice>?): Boolean = fieldValue != null
        }
    )


    fun getFormData() = viewModelScope.launch {
        registerSubscriptionUiState.emit(LoadingData(true))
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
            val unconfirmedOnusJob = async { repository.getUnconfirmedOnus() }

            val genericDevices = genericDevicesJob.await()
            val plans = plansJob.await()
            val places = placeJob.await()
            val technicians = deferredTechnicians.await()
            val napBoxes = napBoxesJob.await()
            val coreDevices = coreDevicesJob.await()
            val unconfirmedOnus = unconfirmedOnusJob.await()
            registerSubscriptionUiState.emit(
                FormDataFound(
                    plans, genericDevices, places,
                    technicians, napBoxes, coreDevices, unconfirmedOnus
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
            registerSubscriptionUiState.emit(FormDataError(e.message.toString()))
        }
        finally {
            registerSubscriptionUiState.emit(LoadingData(false))
        }
    }

    fun getFiberDevices() = viewModelScope.launch {
        fiberCpeDevices.collectLatest {
            it?.let {
                registerSubscriptionUiState.emit(FiberDevicesFound(it))
            }
        }
    }

    fun getWirelessDevices() = viewModelScope.launch {
        wirelessCpeDevices.collectLatest {
            registerSubscriptionUiState.emit(WirelessDevicesFound(it!!))

        }
    }

    fun registerSubscription() = viewModelScope.launch {
        try {
            if (!formIsValid()) return@launch
            val subscription = createSubscription()
            val subscriptionFromRepository = repository.registerSubscription(subscription)
            registerSubscriptionUiState.emit(
                RegisterSubscriptionSuccess(subscriptionFromRepository)
            )
        } catch (error: Exception) {
            registerSubscriptionUiState.emit(RegisterSubscriptionError(error.message.toString()))
        }
    }

    private fun createSubscription(): Subscription {

       return when (installationType) {
            InstallationType.FIBER -> {
                 Subscription(
                    firstName = firstNameField.value,
                    lastName = lastNameField.value,
                    dni = dniField.value,
                    address = addressField.value,
                    phone = phoneField.value,
                    subscriptionDate = subscriptionDateField.value,
                    planId = planField.value?.id,
                    additionalDeviceIds = additionalNetworkDevicesList.map { it.id!! },
                    placeId = placeField.value?.id,
                    technicianId = technicianField.value?.id,
                    napBoxId = napBoxField.value?.id,
                    hostDeviceId = hostDeviceField.value?.id,
                    location = GeoLocation(
                        locationField.value?.latitude ?: 0.0,
                        locationField.value?.longitude ?: 0.0
                    ),
                    cpeDeviceId = cpeDeviceField.value?.id,
                    onu = onuField.value,
                    installationType = installationType
                )
            }
            InstallationType.WIRELESS -> {
                Subscription(
                    firstName = firstNameField.value,
                    lastName = lastNameField.value,
                    dni = dniField.value,
                    address = addressField.value,
                    phone = phoneField.value,
                    subscriptionDate = subscriptionDateField.value,
                    planId = planField.value?.id,
                    additionalDeviceIds = additionalNetworkDevicesList.map { it.id!! },
                    placeId = placeField.value?.id,
                    technicianId = technicianField.value?.id,
                    napBoxId = null,
                    hostDeviceId = hostDeviceField.value?.id,
                    location = GeoLocation(
                        locationField.value?.latitude ?: 0.0,
                        locationField.value?.longitude ?: 0.0
                    ),
                    cpeDeviceId = cpeDeviceField.value?.id,
                    onu = null,
                    installationType = installationType
                )
            }
            null -> throw Exception("Invalid Installation Type")
        }

    }

    private fun formIsValid(): Boolean {

        val fields = when (installationType) {
            InstallationType.FIBER -> listOf(
                firstNameField,
                lastNameField,
                dniField,
                addressField,
                phoneField,
                locationField,
                planField,
                placeField,
                technicianField,
                hostDeviceField,
                subscriptionDateField,
                cpeDeviceField,
                napBoxField,
                onuField
            )
            InstallationType.WIRELESS -> listOf(
                firstNameField,
                lastNameField,
                dniField,
                addressField,
                phoneField,
                locationField,
                planField,
                placeField,
                technicianField,
                hostDeviceField,
                subscriptionDateField,
                cpeDeviceField,
            )
            null -> emptyList()
        }

        fields.forEach {
            it.emitErrorIfExists()
        }

        return fields.all { it.isValid } && installationType != null
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
