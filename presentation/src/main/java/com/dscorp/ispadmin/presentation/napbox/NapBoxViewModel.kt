package com.dscorp.ispadmin.presentation.napbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscription.SubscriptionFormError
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NapBoxViewModel : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val napBoxResponseLiveData = MutableLiveData<NapBoxResponse>()
    val formErrorLiveData = MutableLiveData<NapBoxFormError>()

    fun registerNapBox(
        code: String,
        address: String,
        location: GeoLocation,
    ) {
        if (code.isEmpty()) {
            formErrorLiveData.postValue(NapBoxFormError.OnEtNameNapBoxError("El codigo no puede estar vacio"))
            return
        }
        if (address.isEmpty()) {
            formErrorLiveData.postValue(NapBoxFormError.OnEtAbbreviationError("la direccion no puede estar vacia"))
            return
        }
        if (location == null) {
            formErrorLiveData.postValue(NapBoxFormError.OnEtLocationError("La ubicacion no puede estar vacia"))
            return
        }

        val napBoxObject = NapBox(code = code, address = address, location = location)

        viewModelScope.launch {
            try {
                val napBoxFromRepository = repository.registerNapBox(napBoxObject)
                napBoxResponseLiveData.postValue(
                    NapBoxResponse.OnNapBoxRegister(
                        napBoxFromRepository
                    )
                )
            } catch (error: Exception) {
                napBoxResponseLiveData.postValue(NapBoxResponse.OnError(error))
            }
        }
    }
}




