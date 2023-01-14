package com.dscorp.ispadmin.presentation.technician

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Technician
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class TechnicianViewModel : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val technicianResponseLiveData = MutableLiveData<TechnicianResponse>()
    val technicianFromErrorLiveData = MutableLiveData<TechnicianFromError>()

    fun registerTechnician(
        firstName: String,
        lastName: String,
        dni: String,
        type: String,
        userName: String,
        password: String,
        address: String,
        phone: String,
        birthday: Long
    ) {
        if (firstName.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtFirstNameError(
                    "El nombre del tecnico no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (lastName.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtLastNameError(
                    "El apellido del tecnico no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (dni.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtDniError(
                    "El numero de dni no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (type.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtTypeError(
                    "el typo de tecnico no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (userName.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtUserNameError(
                    "El Usuario no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (password.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtPasswordError(
                    "La contrasena no  " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }

        if (address.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtAddressError(
                    "La direccion no" +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (phone.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtPhoneError(
                    "El numero de telefono no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }
        if (birthday == 0L) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtBirthdayError(
                    "El Cumpleanos no " +
                            "puede estar " +
                            "vacio"
                )
            )
            return
        }

        val technicianObject = Technician(
            name = firstName,
            lastName = lastName,
            dni = dni,
            type = type,
            username = userName,
            password = password,
            address = address,
            phone = phone,
            birthday = birthday
        )

        viewModelScope.launch {
            try {
                val technicianFromRepository = repository.registerTechnician(technicianObject)
                technicianResponseLiveData.postValue(
                    TechnicianResponse.OnTechnicianRegistered(
                        technicianFromRepository
                    )
                )
            } catch (error: Exception) {
                technicianResponseLiveData.postValue(TechnicianResponse.OnError(error))
            }
        }
    }
}