package com.dscorp.ispadmin.presentation.technician

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Technician
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class TechnicianViewModel : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val technicianResponseLiveData = MutableLiveData<TechnicianResponse>()
    val technicianFromErrorLiveData = MutableLiveData<TechnicianFromError>()

    fun registerTechnician(technician: Technician) = viewModelScope.launch {

        try {
            if (formIsValid(technician)) {
                val technicianFromRepository = repository.registerTechnician(technician)
                technicianResponseLiveData.postValue(
                    TechnicianResponse.OnTechnicianRegistered(
                        technicianFromRepository
                    )
                )
            }
        } catch (error: Exception) {
            technicianResponseLiveData.postValue(TechnicianResponse.OnError(error))
        }
    }

    private fun formIsValid(technician: Technician): Boolean {

        if (technician.firstName.isEmpty()) {
            technicianFromErrorLiveData.postValue(
                TechnicianFromError.OnEtFirstNameError("El nombre del tecnico no puede estar vacio"))

            return false
        }
        if (technician.lastName.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtLastNameError(
                    "El apellido del tecnico no puede estar vacio"))

            return false
        }
        if (technician.dni.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtDniError(
                    "El numero de dni no puede estar vacio"))

            return false
        }
        if (technician.type.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtTypeError(
                    "el typo de tecnico no puede estar vacio"))

            return false
        }
        if (technician.username.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtUserNameError(
                    "El Usuario no puede estar vacio"))

            return false
        }
        if (technician.password.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtPasswordError(
                    "La contrasena no puede estar vacio"))

            return false
        }

        if (technician.address.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtAddressError(
                    "La direccion no puede estar vacio"))

            return false
        }
        if (technician.phone.isEmpty()) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtPhoneError(
                    "El numero de telefono no puede estar vacio"))

            return false
        }
        if (technician.birthday == 0L) {
            technicianFromErrorLiveData.postValue(TechnicianFromError.OnEtBirthdayError(
                    "El Cumpleanos no puede estar vacio"))

            return false
        }
        return true

    }
}
