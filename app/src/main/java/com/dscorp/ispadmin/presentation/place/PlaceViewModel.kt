package com.dscorp.ispadmin.presentation.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.IdAbbreviation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val placeResponseLiveData = MutableLiveData<Response>()
    val formErrorLiveData = MutableLiveData<FormError>()

    fun registerPlace(
        namePlace: String,
        abbreviation: String,
    ) {
        if (namePlace.isEmpty()) {
            formErrorLiveData.postValue(FormError.OnEtNamePlaceError("El nombre del lugar no puede estar vacio"))
            println("Debes escribir un  nombre del lugar")
            return
        }
        if (abbreviation.isEmpty()) {
            formErrorLiveData.postValue(FormError.OnEtAbbreviationError("La abreviatura no puede estar vacia"))
            println("FALTA EDITAR FALTA EDITAR")
            return
        }

        val placeObject = IdAbbreviation(
            abbreviation = abbreviation,
            name = namePlace
        )

        viewModelScope.launch {
            try {
                val placeFromRepository = repository.registerPlace(placeObject)
                placeResponseLiveData.postValue(Response.OnPlaceRegister(placeFromRepository))
            } catch (error: Exception) {
                placeResponseLiveData.postValue(Response.OnError(error))
            }
        }
    }
}




