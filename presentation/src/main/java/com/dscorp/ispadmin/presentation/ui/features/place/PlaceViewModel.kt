package com.dscorp.ispadmin.presentation.ui.features.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Place
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class PlaceViewModel : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val placePlaceResponseLiveData = MutableLiveData<PlaceResponse>()
    val formErrorLiveData = MutableLiveData<FormError>()

    fun registerPlace(place: Place) = viewModelScope.launch {

        if (formIsInvalid(place)) this.cancel()

        try {
            val placeFromRepository = repository.registerPlace(place)
            placePlaceResponseLiveData.postValue(PlaceResponse.OnPlaceRegister(placeFromRepository))
        } catch (error: Exception) {
            placePlaceResponseLiveData.postValue(PlaceResponse.OnError(error))
        }

    }

    private fun formIsInvalid(place: Place): Boolean {
        if (place.name.isEmpty()) {
            formErrorLiveData.postValue(FormError.OnEtNamePlaceError("El nombre del lugar no puede estar vacio"))
            println("Debes escribir un  nombre del lugar")
            return true
        }
        if (place.abbreviation.isEmpty()) {
            formErrorLiveData.postValue(FormError.OnEtAbbreviationError("La abreviatura no puede estar vacia"))
            println("FALTA EDITAR FALTA EDITAR")
            return true
        }
        if (place.location == null) {
            formErrorLiveData.postValue(FormError.OnEtLocationError("La ubicacion no puede estar vacia"))
            return true
        }
        return false
    }
}




