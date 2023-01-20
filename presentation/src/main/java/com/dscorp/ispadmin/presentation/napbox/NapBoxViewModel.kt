package com.dscorp.ispadmin.presentation.napbox

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

    private fun formIsValid(napbox: NapBox): Boolean {

        if (napbox.code.isEmpty()) {
            formErrorLiveData.postValue(NapBoxFormError.OnEtNameNapBoxError("El codigo no puede estar vacio"))
            return false
        }
        if (napbox.address.isEmpty()) {
            formErrorLiveData.postValue(NapBoxFormError.OnEtAbbreviationError("la direccion no puede estar vacia"))
            return false
        }
        if (napbox.location == null) {
            formErrorLiveData.postValue(NapBoxFormError.OnEtLocationError("La ubicacion no puede estar vacia"))
            return false
        }
        return true
    }
}




