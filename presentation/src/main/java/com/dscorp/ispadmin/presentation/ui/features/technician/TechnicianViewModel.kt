package com.dscorp.ispadmin.presentation.ui.features.technician

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class TechnicianViewModel : ViewModel() {

    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val technicianResponseLiveData = MutableLiveData<TechnicianResponse>()
    val technicianFromErrorLiveData = MutableLiveData<TechnicianFromError>()
    val technicianErrorCleanFormLiveData = MutableLiveData<TechnicianErrorCleanForm>()

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
            technicianFromErrorLiveData.value =
                (TechnicianFromError.OnEtFirstNameError("El nombre del tecnico no puede estar vacio"))
            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtFirstNameError
        }
        if (technician.lastName.isEmpty()) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtLastNameError(
                        "El apellido del tecnico no puede estar vacio"
                    )
                    )

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtLastNameError
        }
        if (technician.dni.isEmpty()) {
            technicianFromErrorLiveData.value = (TechnicianFromError.OnEtDniError(
                "El numero de dni no puede estar vacio"
            ))
            return false
        }
        if (technician.dni.length < 8) {
            technicianFromErrorLiveData.value = (TechnicianFromError.OnEtDniError(
                "DNI Requiere 8 caracteres"
            ))
            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtDniError
        }
        if (technician.type.isEmpty()) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtTypeError(
                        "el tipo de tecnico no puede estar vacio"
                    )
                    )

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtTypeError
        }
        if (technician.username.isEmpty()) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtUserNameError(
                        "El Usuario no puede estar vacio"
                    )
                    )

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtUserNameError
        }
        if (technician.password.isEmpty()) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtPasswordError(
                        "La contrasena no puede estar vacio"
                    )
                    )

            return false
        }
        if (technician.password.length < 8) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtPasswordError(
                        "La contraseña requiere entre 8 y 20 caracteres"
                    )
                    )

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtPasswordError
        }

        if (technician.address.isEmpty()) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtAddressError(
                        "La direccion no puede estar vacio"
                    )
                    )

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtAddressError
        }
        if (technician.phone.isEmpty()) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtPhoneError(
                        "El numero de telefono no puede estar vacio"
                    )
                    )

            return false
        }
        if (technician.phone.length <9) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtPhoneError(
                        "El numero de telefono requiere 9 caracteres"
                    )
                    )

            return false
        }else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtPhoneError
        }
        if (technician.birthday == 0L) {
            technicianFromErrorLiveData.value = (
                    TechnicianFromError.OnEtBirthdayError(
                        "El Cumpleanos no puede estar vacio"
                    )
                    )

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtBirthdayError
        }
        return true

    }
}
