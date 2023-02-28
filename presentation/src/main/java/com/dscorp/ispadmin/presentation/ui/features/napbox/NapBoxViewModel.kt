package com.dscorp.ispadmin.presentation.ui.features.napbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.napbox.edit.EditNapBoxFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.napbox.edit.EditNapBoxUiState
import com.dscorp.ispadmin.presentation.ui.features.napbox.register.CleanFormErrors
import com.dscorp.ispadmin.presentation.ui.features.napbox.register.NapBoxFormError
import com.dscorp.ispadmin.presentation.ui.features.napbox.register.NapBoxSealedClassResponse
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NapBoxViewModel : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

     val editNapBoxUiState = MutableLiveData<EditNapBoxUiState>()
     val editFormErrorLiveData = MutableLiveData<EditNapBoxFormErrorUiState>()

    val napBoxSealedClassResponseLiveData = MutableLiveData<NapBoxSealedClassResponse>()
    val formErrorLiveData = MutableLiveData<NapBoxFormError>()

    private val cleanErrorLiveData = MutableLiveData<CleanFormErrors>()

    var napBoxResponse:NapBoxResponse? = null
    fun registerNapBox(registerNapBox: NapBox) = viewModelScope.launch {
        try {
            if (formIsValid(registerNapBox)) {
                val registerNapBoxFromRepository = repository.registerNapBox(registerNapBox)
                napBoxSealedClassResponseLiveData.postValue(
                    NapBoxSealedClassResponse.OnNapBoxSealedClassRegister(
                        registerNapBoxFromRepository
                    )
                )
            }
        } catch (error: Exception) {
            napBoxSealedClassResponseLiveData.postValue(NapBoxSealedClassResponse.OnError(error))
        }
    }
    fun editNapBox(napBox: NapBox) = viewModelScope.launch {
        try {
            if (!editFormIsValid(napBox)) return@launch
            val response = repository.editNapBox(napBox)
            editNapBoxUiState.postValue(
                EditNapBoxUiState.EditNapBoxSuccess(
                    response
                )
            )
        } catch (e: Exception) {
            editNapBoxUiState.postValue(EditNapBoxUiState.EditNapBoxError(e.message))
        }
    }

    private fun formIsValid(napBox: NapBox): Boolean {

        if (napBox.code.isEmpty()) {
            formErrorLiveData.value = NapBoxFormError.OnEtCodeError()
            return false
        } else {
            cleanErrorLiveData.value = CleanFormErrors.OnEtCodeCleanError
        }
        if (napBox.address.isEmpty()) {
            formErrorLiveData.value = NapBoxFormError.OnEtAddressError()
            return false
        } else {
            cleanErrorLiveData.value = CleanFormErrors.OnEtAddressCleanError
        }
        if (napBox.location == null) {
            formErrorLiveData.value = NapBoxFormError.OnEtLocationError()
            return false
        } else {
            cleanErrorLiveData.value = CleanFormErrors.OnEtLocationCleanError
        }
        return true
    }
    private fun editFormIsValid(napBox: NapBox): Boolean {

        if (napBox.code.isEmpty()) {
            editFormErrorLiveData.value = EditNapBoxFormErrorUiState.OnEtCodeError()
            return false
        } else {
            editFormErrorLiveData.value = EditNapBoxFormErrorUiState.OnEtCodeCleanError
        }
        if (napBox.address.isEmpty()) {
            editFormErrorLiveData.value = EditNapBoxFormErrorUiState.OnEtAddressError()
            return false
        } else {
            editFormErrorLiveData.value = EditNapBoxFormErrorUiState.OnEtAddressCleanError
        }
        if (napBox.location == null) {
            editFormErrorLiveData.value = EditNapBoxFormErrorUiState.OnEtLocationError()
            return false
        } else {
            editFormErrorLiveData.value = EditNapBoxFormErrorUiState.OnEtLocationCleanError
        }


        return true
    }
}
