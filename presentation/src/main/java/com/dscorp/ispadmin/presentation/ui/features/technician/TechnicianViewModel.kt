package com.dscorp.ispadmin.presentation.ui.features.technician

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.entity.Technician
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

        if (technician.name.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtFirstNameError()
            return false
        } else {
            technicianErrorCleanFormLiveData.value =
                TechnicianErrorCleanForm.OnEtFirstNameCleanError
        }
        if (technician.lastName.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtLastNameError()
            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtLastNameCleanError
        }
        if (technician.dni.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtDniError()
            return false
        }
        if (technician.dni.length < 8) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtDniError()
            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtDniCleanError
        }
        if (technician.type.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtTypeError()

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtTypeCleanError
        }
        if (technician.username.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtUserNameError()
            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtUserNameCleanError
        }
        if (technician.password.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtPasswordError()
            return false
        }
        if (technician.password.length < 8) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtPasswordIsInvalidError()

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtPasswordCleanError
        }

        if (technician.address.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtAddressError()

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtAddressCleanError
        }
        if (technician.phone.isEmpty()) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtPhoneError()

            return false
        }
        if (technician.phone.length < 9) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtPhoneError()

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtPhoneCleanError
        }
        if (technician.birthday == 0L) {
            technicianFromErrorLiveData.value = TechnicianFromError.OnEtBirthdayError()

            return false
        } else {
            technicianErrorCleanFormLiveData.value = TechnicianErrorCleanForm.OnEtBirthdayCleanError
        }
        return true
    }
}
