package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.extension.formIsValid
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.ReactiveFormField
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.domain.entity.InstallationType
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidPhone
import com.example.data2.data.repository.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RegisterSubscriptionViewModel(
    private val repository: IRepository,
) : BaseViewModel<RegisterSubscriptionUiState>() {

    var subscription: SubscriptionResponse? = null
    var installationType = MutableLiveData(InstallationType.FIBER)
    var selectedAdditionalDevice = MutableLiveData<NetworkDevice?>(null)
    val addButtonIsEnabled = selectedAdditionalDevice.map { it != null }
    var additionalNetworkDevicesList = mutableSetOf<NetworkDevice>()
    private val cpeDevices = MutableStateFlow<List<NetworkDevice>?>(null)

    private val fiberCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.FIBER_ROUTER }
    }

    private val wirelessCpeDevices = cpeDevices.map { cpeDevices ->
        cpeDevices?.filter { it.networkDeviceType == NetworkDevice.NetworkDeviceType.WIRELESS_ROUTER }
    }

    private val plans = MutableStateFlow<List<PlanResponse>?>(null)

    private val plansFiber = plans.map { plans ->
        plans?.filter { it.type == PlanResponse.PlanType.FIBER }
    }

    private val plansWireless = plans.map { plans ->
        plans?.filter { it.type == PlanResponse.PlanType.WIRELESS }
    }


    val firstNameField = ReactiveFormField<String?>(
        hintResourceId = R.string.name,
        errorResourceId = R.string.fieldMustNotBeEmpty
    ) { !it.isNullOrEmpty() }

    val lastNameField = ReactiveFormField<String?>(
        hintResourceId = R.string.lastName,
        errorResourceId = R.string.fieldMustNotBeEmpty
    ) { !it.isNullOrEmpty() }

    val dniField = ReactiveFormField<String?>(
        hintResourceId = R.string.dni,
        errorResourceId = R.string.invalidDNI
    ) { it.isValidDni() }

    val addressField = ReactiveFormField<String?>(
        hintResourceId = R.string.address,
        errorResourceId = R.string.fieldMustNotBeEmpty
    ) { !it.isNullOrEmpty() }

    val phoneField = ReactiveFormField<String?>(
        hintResourceId = R.string.phoneNumer,
        errorResourceId = R.string.invalidPhoneNumber
    ) { it.isValidPhone() }

    val couponField = ReactiveFormField<String?>(
        hintResourceId = R.string.coupon,
        errorResourceId = R.string.fieldMustNotBeEmpty
    ) { true }

    val priceField = ReactiveFormField<String?>(
        hintResourceId = R.string.price,
        errorResourceId = R.string.invalidPrice
    ) { (it != null) && it.isNotEmpty() && (it.toDouble() > 0) }

    val locationField = ReactiveFormField<LatLng?>(
        hintResourceId = R.string.location,
        errorResourceId = R.string.mustSelectLocation
    ) { it != null }

    val planField = ReactiveFormField<PlanResponse?>(
        hintResourceId = R.string.plan,
        errorResourceId = R.string.mustSelectPlan
    ) { it != null }

    val placeField = ReactiveFormField<PlaceResponse?>(
        hintResourceId = R.string.place,
        errorResourceId = R.string.mustSelectPlace
    ) { it != null }

    val technicianField = ReactiveFormField<Technician?>(
        hintResourceId = R.string.technician,
        errorResourceId = R.string.mustSelectTechnician
    ) { it != null }

    val hostDeviceField = ReactiveFormField<NetworkDevice?>(
        hintResourceId = R.string.host_device,
        errorResourceId = R.string.mustSelectHostDevice
    ) { it != null }

    val subscriptionDateField = ReactiveFormField<Long?>(
        hintResourceId = R.string.subscriptionDate,
        errorResourceId = R.string.mustSelectSubscriptionDate
    ) { it != null }

    val isMigrationField = ReactiveFormField<Boolean?>(
        hintResourceId = R.string.empty,
        errorResourceId = R.string.empty
    ) { true }

    val cpeDeviceField = ReactiveFormField<NetworkDevice?>(
        hintResourceId = R.string.select_cpe_device,
        errorResourceId = R.string.mustSelectCpeDevice
    ) { it != null }

    val napBoxField = ReactiveFormField<NapBoxResponse?>(
        hintResourceId = R.string.selec_nap_box,
        errorResourceId = R.string.mustSelectNapBox
    ) { it != null }

    val onuField = ReactiveFormField<Onu?>(
        hintResourceId = R.string.select_onu,
        errorResourceId = R.string.mustSelectOnu
    ) { it != null }

    val additionalDevicesField = ReactiveFormField<NetworkDevice?>(
        hintResourceId = R.string.additionalDevices,
        errorResourceId = R.string.youCanSelectAdditionalNetworkDevices
    ) { true }

    val noteField = ReactiveFormField<String?>(
        hintResourceId = R.string.note,
        errorResourceId = R.string.errorNote
    ) { true }

    fun getFormData() =
        executeNoProgress(onSuccess = {
            uiState.value = BaseUiState(uiState = ShimmerVisibility(false))
        }) {
            val cpeDevicesJob = it.async { repository.getCpeDevices() }
            val cpeDevicesList = cpeDevicesJob.await()
            cpeDevices.value = cpeDevicesList

            val genericDevicesJob = it.async { repository.getGenericDevices() }
            val plansJob = it.async { repository.getPlans() }
            val placeJob = it.async { repository.getPlaces() }
            val napBoxesJob = it.async { repository.getNapBoxes() }
            val deferredTechnicians = it.async { repository.getTechnicians() }
            val coreDevicesJob = it.async { repository.getCoreDevices() }
            val unconfirmedOnusJob = it.async { repository.getUnconfirmedOnus() }

            val genericDevices = genericDevicesJob.await()
            this.plans.value = plansJob.await()
            val places = placeJob.await()
            val technicians = deferredTechnicians.await()
            val napBoxes = napBoxesJob.await()
            val coreDevices = coreDevicesJob.await()
            val unconfirmedOnus = unconfirmedOnusJob.await()
            uiState.value = BaseUiState(
                uiState = FormDataFound(
                    genericDevices, places,
                    technicians, napBoxes, coreDevices, unconfirmedOnus
                )
            )
        }

    fun getOnuData() = executeNoProgress(doFinally = {
        uiState.value = BaseUiState(uiState = RefreshingOnus(false))
    }) {
        uiState.value = BaseUiState(uiState = RefreshingOnus(true))
        val unconfirmedOnus = repository.getUnconfirmedOnus()
        uiState.value = BaseUiState(uiState = OnOnuDataFound(unconfirmedOnus))
    }


    fun getFiberPlans() = executeNoProgress {
        plansFiber.collectLatest {
            it?.let {
                uiState.value = BaseUiState(PlansFound(it))
            }
        }
    }

    fun getWirelessPlans() = executeNoProgress {
        plansWireless.collectLatest {
            it?.let {
                uiState.value = BaseUiState(PlansFound(it))
            }
        }
    }

    fun getFiberDevices() = executeNoProgress {
        fiberCpeDevices.collectLatest {
            it?.let {
                uiState.value = BaseUiState(FiberDevicesFound(it))
            }
        }
    }

    fun getWirelessDevices() = viewModelScope.launch {
        wirelessCpeDevices.collectLatest {
            uiState.value = BaseUiState(WirelessDevicesFound(it!!))
        }
    }

    fun activateCoupon() = executeWithProgress {
        val response = repository.applyCoupon(couponField.getValue()!!)
        if (response != null) uiState.value = BaseUiState(CouponIsValid(true))
        else uiState.value = BaseUiState(CouponIsValid(false))
    }

    fun registerSubscription() = executeWithProgress {
        if (!formIsValid()) return@executeWithProgress
        val subscription = createSubscription()
        val subscriptionFromRepository = repository.registerSubscription(subscription)
        uiState.value = BaseUiState(
            RegisterSubscriptionSuccess(subscriptionFromRepository)
        )
    }

    private fun createSubscription(): Subscription {

        val subscription = Subscription(
            firstName = firstNameField.getValue(),
            lastName = lastNameField.getValue(),
            dni = dniField.getValue(),
            address = addressField.getValue(),
            phone = phoneField.getValue(),
            subscriptionDate = subscriptionDateField.getValue(),
            planId = planField.getValue()?.id,
            additionalDeviceIds = additionalNetworkDevicesList.map { it.id!! },
            placeId = placeField.getValue()?.id,
            technicianId = technicianField.getValue()?.id,
            hostDeviceId = hostDeviceField.getValue()?.id,
            location = GeoLocation(
                locationField.getValue()?.latitude ?: 0.0,
                locationField.getValue()?.longitude ?: 0.0
            ),
            cpeDeviceId = cpeDeviceField.getValue()?.id,
            installationType = installationType.value,
            price = priceField.getValue()?.toDouble(),
            coupon = couponField.getValue(),
            isMigration = isMigrationField.getValue(),
            note = noteField.getValue()
        )

        return when (installationType.value) {
            InstallationType.FIBER -> subscription.apply {
                napBoxId = napBoxField.getValue()?.id
                onu = onuField.getValue()
            }

            InstallationType.WIRELESS -> subscription
            else -> throw Exception("Invalid Installation Type")
        }

    }

    private fun formIsValid(): Boolean {

        val formFields = mutableListOf(
            firstNameField,
            lastNameField,
            dniField,
            addressField,
            phoneField,
            priceField,
            locationField,
            planField,
            placeField,
            technicianField,
            hostDeviceField,
            subscriptionDateField,
            cpeDeviceField,
        )

        if (installationType.value == InstallationType.FIBER) {
            formFields.apply {
                add(napBoxField)
                add(onuField)
            }
        }

        return formFields.formIsValid()
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

    fun onIsMigrationCheckedChanged(button: CompoundButton, isChecked: Boolean) {
        isMigrationField.liveData.value = isChecked
    }

}
