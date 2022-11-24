package com.dscorp.ispadmin.presentation.registration

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sergio Carrillo Diestra on 18/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val userLiveData: MutableLiveData<User> = MutableLiveData()
    val errorLiveData: MutableLiveData<Exception> = MutableLiveData()
    fun validateForm(
        user: String,
        password1: String,
        password2: String,
        firstName: String,
        lastName: String,
        etUser: EditText,
        etFirstName: EditText,
        etLastName: EditText,
        etPassword1: EditText,
        etPassword2: EditText
    ) {

        if (user.isEmpty()) {

            println("debes escribir un usuario")
            etUser.setError("el usuario no puede estar vacio")
            return
        }

        if (firstName.isEmpty()) {

            println("debes escribir un nombre")
            etFirstName.setError("el nombre no puede estar vacio")
            return
        }

        if (lastName.isEmpty()) {

            println("debes escribir un apellido")
            etLastName.setError("el apellido no puede estar vacio")
            return
        }
        if (password1.isEmpty()) {
            println("debes escribir una contrasena")
            etPassword1.setError("la contrasena no puede estar vacia")
            return
        }

        if (password2.isEmpty()) {
            println("debes escribir una contrasena")
            etPassword2.setError("la contrasena no puede estar vacia")
            return
        }


        if (password1 != password2) {
            println("Las Contrasenas No Coinciden")
            etPassword1.setError("las contrasenas no coinciden")
            etPassword2.setError("las contrasenas no coinciden")
            return


        }
        var userToRegister: User =
            User(
                name = firstName,
                lastName = lastName,
                type = 0,
                username = user,
                password = password1,
                verified = false)

        viewModelScope.launch {
          try {
              var response = repository.registerUser(userToRegister)
              userLiveData.postValue(response)

          }catch (error:Exception){
              errorLiveData.postValue(error)

          }


        }

    }

}