package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionResponse.*
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class RegisterSubscriptionViewModel : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)
    val responseLiveData = MutableLiveData<RegisterSubscriptionResponse>()
    val formErrorLiveData = MutableLiveData<RegisterSubscriptionFormError>()

    init {
        getFormData()
    }

    private fun getFormData() = viewModelScope.launch {
        try {
            val devicesJob = async { repository.getDevices() }
            val plansJob = async { repository.getPlans() }
            val placeJob = async { repository.getPlaces() }
            val napBoxesJob = async { repository.getNapBoxes() }
            val deferredTechnicians = async { repository.getTechnicians() }
            val devicesFromRepository = devicesJob.await()
            val plansFromRepository = plansJob.await()
            val placeFromRepository = placeJob.await()
            val technicians = deferredTechnicians.await()
            val napBoxesFromRepository = napBoxesJob.await()

            responseLiveData.postValue(
                OnFormDataFound(
                    plansFromRepository, devicesFromRepository, placeFromRepository,
                    technicians, napBoxesFromRepository))

        } catch (e: Exception) {
            responseLiveData.postValue(OnError(e))
        }
    }

    fun registerSubscription(subscription: Subscription) = viewModelScope.launch {
        try {
            if (formIsValid(subscription)) {
                val subscriptionFromRepository = repository.doSubscription(subscription)
                responseLiveData.postValue(OnRegisterSubscriptionRegistered(subscriptionFromRepository))
            }

        } catch (error: Exception) {
            responseLiveData.postValue(OnError(error))

        }
    }

    private fun formIsValid(subscription: Subscription): Boolean {

        if (subscription.firstName.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtFirstNameError("Ingresa tu nombre. ")
            return false

        }else{
            formErrorLiveData.value = RegisterSubscriptionFormError.OnEtFirstNameHasNotErrors

        }

        if (subscription.lastName.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtLastNameError("Ingresa Tu Apellido")
            return false
        }else{
            formErrorLiveData.value = RegisterSubscriptionFormError.OnEtLastNameHasNotErrors

        }

        if (subscription.dni.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtDniError("Ingresa tu nuemero de DNI.")
            return false
        }
        if (subscription.dni.length<8) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnDniIsInvalidError("El DNI debe tener 8 caracteres")
            return false
        }
        if (subscription.password.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtPasswordError("Ingresa una contrasena.")
            return false
        }

        if (subscription.address.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtAddressesError("Ingresa una Direccion")
            return false
        }

        if (subscription.phone.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtNumberPhoneError("Ingresa tu número de telefono")
            return false
        }else{
            formErrorLiveData.value = RegisterSubscriptionFormError.OnEtFirstNameHasNotErrors

        }

        if (subscription.phone.length<9) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnPhoneIsInvalidError("El Numero de telefono debe tener 9 caracteres")
            return false
        }

        if (subscription.subscriptionDate == 0L) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtSubscriptionDateError("Debes colocar una fecha de suscripcion")
            return false
        }

        if (subscription.location == null) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnEtLocationError("La ubicacion no puede estar vacia")
            return false
        }

        if (subscription.planId.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnSpnPlanError("Debes seleccionar un plan")
            return false
        }

        if (subscription.networkDeviceId.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnSpnNetworkDeviceError("Debes seleccionar un dispositivo de red")
            return false
        }

        if (subscription.placeId.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnSpnPlaceError("Debes seleccionar un lugar")
            return false
        }
        if (subscription.napBoxId.isEmpty()) {
            formErrorLiveData.value=RegisterSubscriptionFormError.OnSpnNapBoxError("Debes seleccionar una Caja Nap")
            return false
        }

        if (subscription.networkDeviceId.isEmpty()) {
            formErrorLiveData.value=
                RegisterSubscriptionFormError.OnSpnNetworkDeviceError("Debes seleccionar un técnico")

            return false
        }

        return true
    }
}






