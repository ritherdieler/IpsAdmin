package com.dscorp.ispadmin.presentation.Subscription

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
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

     init {
         viewModelScope.launch {
              var response = repository.getplans()
              planListLiveData.postValue(response)


         }
     }

     fun validateform(
          etFirstName: EditText,
          etLastName: EditText,
          etPassword: EditText,
          etDni: EditText,
          etAddress: EditText,
          etPhone: EditText,
          etSubscriptionDate: EditText,
          firstname:String,
          lastname:String,
          password:String,
          dni:String,
          address:String,
          phone:String,
          subscriptionDate:Int,

     ) {
          if (firstname.isEmpty()){
               println("Ingresa tu nombre. ")
               etFirstName.setError("este campo no puede estar vacio")
          }
          if (lastname.isEmpty()){
               println("Ingresa tu apellido. ")
               etLastName.setError("este campo no puede estar vacio")
          }
          if (password.isEmpty()){
               println("Ingresa una contrasena.")
               etPassword.setError("este campo no puede estar vacio")
          }
          if (dni.isEmpty()){
               println("Ingresa tu nuemero de DNI.")
               etDni.setError("este campo no puede estar vacio")
          }
          if (address.isEmpty()){
               println("Ingresa una direccion")
               etAddress.setError("este campo no puede estar vacio")
          }
          if (phone.isEmpty()){
               println("Ingresa un numero valido ")
               etPhone.setError("este campo no puede estar vacio")
          }
          if (subscriptionDate.toString().isEmpty()){
               println("")
               etSubscriptionDate.setError("este campo no puede estar vacio")
          }


          val planObject = Subscription(
               firstName = firstname,
               lastName = lastname,
               dni = dni,
               password =password ,
               address = address,
               phone = phone,
               subscriptionDate = subscriptionDate,
          )

          viewModelScope.launch {
               try {

                    var response = repository.doSubscription(planObject)
                    subscriptionLiveData.postValue(response)

               } catch (error: Exception) {
                    errorLiveData.postValue(error)

               }

          }



     }
}





