package com.dscorp.ispadmin.presentation.Subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.NetworkDevice
import com.dscorp.ispadmin.repository.model.Plan
import com.dscorp.ispadmin.repository.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(private val repository: Repository) : ViewModel() {



     val subscriptionLiveData:MutableLiveData<Subscription> = MutableLiveData ()
     val errorLiveData:MutableLiveData<Exception> = MutableLiveData()
     val planListLiveData:MutableLiveData<List<Plan>> = MutableLiveData()
     val deviceListLiveData:MutableLiveData<List<NetworkDevice>> = MutableLiveData()

     init {
         viewModelScope.launch {
              var response = repository.getplans()
              planListLiveData.postValue(response)
         }
          viewModelScope.launch{
               var response = repository.getDevices()
               deviceListLiveData.postValue(response)

          }
     }


     fun registerSubscription(
          firstname:String,
          lastname:String,
          password:String,
          dni:String,
          address:String,
          phone:String,
          subscriptionDate:Int,
          planId:String,
          networkDeviceId:String
     ) {
          validateform(firstname, lastname, password, dni, address, phone, subscriptionDate,planId,networkDeviceId)
          sendRegisterSubscriptionToServer(firstname, lastname, dni, password, address, phone, subscriptionDate,planId,networkDeviceId)
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

                    var response = repository.doSubscription(subscriptionObjetct)
                    subscriptionLiveData.postValue(response)

               } catch (error: Exception) {
                    errorLiveData.postValue(error)

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
          subscriptionDate: Int,
          planId: String,
          networkDeviceId: String
     ) {
          if (firstname.isEmpty()) {
               println("Ingresa tu nombre. ")
               //etFirstName.setError("este campo no puede estar vacio")
               return
          }
          if (lastname.isEmpty()) {
               println("Ingresa tu apellido. ")
               //etLastName.setError("este campo no puede estar vacio")
               return
          }
          if (password.isEmpty()) {
               println("Ingresa una contrasena.")
               //etPassword.setError("este campo no puede estar vacio")
               return
          }
          if (dni.isEmpty()) {
               println("Ingresa tu nuemero de DNI.")
               //etDni.setError("este campo no puede estar vacio")
               return
          }
          if (address.isEmpty()) {
               println("Ingresa una direccion")
               //etAddress.setError("este campo no puede estar vacio")
               return
          }
          if (phone.isEmpty()) {
               println("Ingresa un numero valido ")
               //etPhone.setError("este campo no puede estar vacio")
               return
          }
          if (subscriptionDate.toString().isEmpty()) {
               println("")
              // etSubscriptionDate.setError("este campo no puede estar vacio")
               return
          }
          if (planId.toString().isEmpty()){
               println("Debes seleccionar un plan")
               return
          }
          if (networkDeviceId.toString().isEmpty()){
               println("Debes seleccionar un dispositivo de red")
               return
          }
     }
}





