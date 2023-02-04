package com.dscorp.ispadmin.presentation.ui.features.napbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NapBoxViewModel : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val napBoxResponseLiveData = MutableLiveData<NapBoxResponse>()
    val formErrorLiveData = MutableLiveData<NapBoxFormError>()
    private val cleanErrorLiveData = MutableLiveData<CleanFormErrors>()

    fun registerNapBox(registerNapBox: NapBox) = viewModelScope.launch {
        try {
            if (formIsValid(registerNapBox)) {
                val registerNapBoxFromRepository = repository.registerNapBox(registerNapBox)
                napBoxResponseLiveData.postValue(
                    NapBoxResponse.OnNapBoxRegister(
                        registerNapBoxFromRepository
                    )
                )
            }
        } catch (error: Exception) {
            napBoxResponseLiveData.postValue(NapBoxResponse.OnError(error))
        }

    }

    private fun formIsValid(napBox: NapBox): Boolean {

        if (napBox.code.isEmpty()) {
            formErrorLiveData.value=NapBoxFormError.OnEtCodeError()
            return false
        }else{
            cleanErrorLiveData.value = CleanFormErrors.OnEtCodeCleanError
        }
        if (napBox.address.isEmpty()) {
            formErrorLiveData.value=NapBoxFormError.OnEtAddressError()
            return false
        }else{
            cleanErrorLiveData.value = CleanFormErrors.OnEtAddressCleanError
        }
        if (napBox.location == null) {
            formErrorLiveData.value=NapBoxFormError.OnEtLocationError()
            return false
        }else{
            cleanErrorLiveData.value = CleanFormErrors.OnEtLocationCleanError
        }
        return true
    }
}




