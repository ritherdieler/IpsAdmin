package com.dscorp.ispadmin.presentation.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscription.SubscriptionResponse.*
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.model.Subscription
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

    fun getFormData() {
        viewModelScope.launch {
            try {
                val devicesJob = async { repository.getDevices() }
                val plansJob = async { repository.getplans() }
                val placeJob = async { repository.getPlaces() }
                val devicesFromRepository = devicesJob.await()
                val plansFromRepository = plansJob.await()
                val placeFromRepository = placeJob.await()

                responseLiveData.postValue(OnFormDataFound(plansFromRepository, devicesFromRepository,
                    placeFromRepository))

            } catch (e: Exception) {
                responseLiveData.postValue(OnError(e))
            }
        }
    }

    fun registerSubscription(
        firstname: String,
        lastname: String,
        password: String,
        dni: String,
        address: String,
        phone: String,
        subscriptionDate: String,
        planId: String,
        networkDeviceId: String,
        placeId: String,
    ) {
        validateform(firstname, lastname, password, dni, address, phone, subscriptionDate, planId, networkDeviceId,
            placeId)
    }

    private fun sendRegisterSubscriptionToServer(
        firstname: String,
        lastname: String,
        dni: String,
        password: String,
        address: String,
        phone: String,
        subscriptionDate: Int,
        planId: String,
        networkDeviceId: String,
        placeId: String,
    ) {
        val subscriptionObjetct = Subscription(
            firstName = firstname,
            lastName = lastname,
            dni = dni,
            password = password,
            address = address,
            phone = phone,
            subscriptionDate = subscriptionDate,
            planId = planId,
            networkDeviceId = networkDeviceId,
            placeId = placeId
        )

        viewModelScope.launch {
            try {

                val subscriptionFromRepository = repository.doSubscription(subscriptionObjetct)
                responseLiveData.postValue(OnSubscriptionRegistered
                    (subscriptionFromRepository))

            } catch (error: Exception) {
                responseLiveData.postValue(OnError(error))

            }
        }
    }

    private fun validateform(
        firstname: String,
        lastname: String,
        password: String,
        dni: String,
        address: String,
        phone: String,
        subscriptionDate: String,
        planId: String,
        networkDeviceId: String,
        placeId: String,
    ) {
        if (firstname.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtFirstNameError("Ingresa tu nombre. "))
            return
        }
        if (lastname.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtLastNameError("Ingresa Tu Apellido"))
            return
        }
        if (dni.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtDniError("Ingresa tu nuemero de DNI."))
            return
        }
        if (password.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtPasswordError("Ingresa una contrasena."))
            return
        }

        if (address.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtAddressesError("Ingresa una Direccion"))
            return
        }
        if (phone.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtNumberPhoneError("Ingresa Un numero valido"))
            return
        }
        if (subscriptionDate.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnEtSubscriptionDateError("Debes colocar una fecha " +
                    "de suscripcion"))
            return
        }
        if (planId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnPlanError("Debes seleccionar un plan"))
            return
        }
        if (networkDeviceId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnNetworkDeviceError("Debes seleccionar un " +
                    "dispositivo de red"))
            return
        }
        if (placeId.isEmpty()) {
            formErrorLiveData.postValue(SubscriptionFormError.OnSpnPlaceError("Debes seleccionar un " +
                    "lugar"))
            return
        }
        sendRegisterSubscriptionToServer(firstname, lastname, dni, password, address, phone, subscriptionDate.toInt(),
            planId, networkDeviceId, placeId)
    }
}





