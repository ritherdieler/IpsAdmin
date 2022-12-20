package com.dscorp.ispadmin.presentation.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

          val responseLiveData=MutableLiveData<SubscriptionResponse>()
          val formErrorLiveData=MutableLiveData<SubscriptionFormError>()

     init {
          getPlans()
          getDevices()
     }

     private fun getPlans() {
          viewModelScope.launch {
               try {
                    val plansFromRepository = repository.getplans()
                    responseLiveData.postValue(SubscriptionResponse.OnPlansFetched(plansFromRepository))
               }catch (e:Exception){
                    responseLiveData.postValue(SubscriptionResponse.OnError(e))
               }
          }
     }

     private fun getDevices() {
          viewModelScope.launch {
               try {
                    val devicesFromRepository = repository.getDevices()
                    responseLiveData.postValue(SubscriptionResponse.OnNetworkDeviceFetched(devicesFromRepository))
               }catch (e:Exception){
                    responseLiveData.postValue(SubscriptionResponse.OnError(e))
               }
          }
     }

     fun registerSubscription(
          firstname:String,
          lastname:String,
          password:String,
          dni:String,
          address:String,
          phone:String,
          subscriptionDate: String,
          planId:String,
          networkDeviceId:String
     ) {
          validateform(firstname, lastname, password, dni, address, phone, subscriptionDate,planId,networkDeviceId)
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
          networkDeviceId: String
     ) {
          val subscriptionObjetct = Subscription(
               firstName = firstname,
               lastName = lastname,
               dni = dni,
               password = password,
               address = address,
               phone = phone,
               subscriptionDate = subscriptionDate,
               planId =planId,
               networkDeviceId =networkDeviceId
          )

          viewModelScope.launch {
               try {

                    val subscriptionFromRepository = repository.doSubscription(subscriptionObjetct)
                    responseLiveData.postValue(SubscriptionResponse.OnSubscriptionRegistered
                         (subscriptionFromRepository))

               } catch (error: Exception) {
                    responseLiveData.postValue(SubscriptionResponse.OnError(error))

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
          networkDeviceId: String
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
          if (planId.isEmpty()){
               formErrorLiveData.postValue(SubscriptionFormError.OnSpnPlanError("Debes seleccionar un plan"))
               return
          }
          if (networkDeviceId.isEmpty()){
               formErrorLiveData.postValue(SubscriptionFormError.OnSpnNetworkDeviceError("Debes seleccionar un " +
                       "dispositivo de red"))
               return
          }
          sendRegisterSubscriptionToServer(firstname, lastname, dni, password, address, phone, subscriptionDate.toInt(),
               planId,networkDeviceId)
     }
}





