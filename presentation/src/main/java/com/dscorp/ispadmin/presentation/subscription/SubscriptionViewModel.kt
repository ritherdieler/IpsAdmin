package com.dscorp.ispadmin.presentation.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscription.SubscriptionResponse.*
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class SubscriptionViewModel : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)
    val responseLiveData = MutableLiveData<SubscriptionResponse>()
    val formErrorLiveData = MutableLiveData<SubscriptionFormError>()

    init {
        getFormData()
    }

    private fun getFormData() = viewModelScope.launch {
        try {
            val devicesJob = async { repository.getDevices() }
            val plansJob = async { repository.getplans() }
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
                responseLiveData.postValue(OnSubscriptionRegistered(subscriptionFromRepository))
            }

        } catch (error: Exception) {
            responseLiveData.postValue(OnError(error))

        }
    }

    private fun formIsValid(subscription: Subscription): Boolean {

        if (subscription.firstName.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtFirstNameError("Ingresa tu nombre. "))
            return false
        }
        if (subscription.lastName.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtLastNameError("Ingresa Tu Apellido"))
            return false
        }
        if (subscription.dni.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtDniError("Ingresa tu nuemero de DNI."))
            return false
        }
        if (subscription.password.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtPasswordError("Ingresa una contrasena."))
            return false
        }

        if (subscription.address.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtAddressesError("Ingresa una Direccion"))
            return false
        }

        if (subscription.phone.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtNumberPhoneError("Ingresa Un numero valido"))
            return false
        }
      /*  if (subscription.phone.isEmpty() || subscription.phone.length < 9) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtNumberPhoneError("Ingresa Un numero valido de 9 digitos"))
            return false
        }*/

        if (subscription.subscriptionDate == 0L) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtSubscriptionDateError("Debes colocar una fecha de suscripcion"))
        }

        if (subscription.location == null) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtLocationError("La ubicacion no puede estar vacia"))
            return false
        }

        if (subscription.planId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnPlanError("Debes seleccionar un plan"))
            return false
        }

        if (subscription.networkDeviceId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnNetworkDeviceError("Debes seleccionar un dispositivo de red"))
            return false
        }

        if (subscription.placeId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnPlaceError("Debes seleccionar un lugar"))
            return false
        }
        if (subscription.napBoxId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnNapBoxError("Debes seleccionar una Caja Nap"))
            return false
        }

        if (subscription.networkDeviceId.isEmpty()) {
            formErrorLiveData.postValue(
                SubscriptionFormError.OnSpnNetworkDeviceError("Debes seleccionar un técnico")
            )
            return false
        }

        return true
    }
}






