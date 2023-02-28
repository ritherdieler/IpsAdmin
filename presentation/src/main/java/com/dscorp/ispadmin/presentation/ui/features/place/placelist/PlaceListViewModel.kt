package com.dscorp.ispadmin.presentation.ui.features.place.placelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaceListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<PlaceListResponse>()

    init {
        initGetSubscriptions()
    }

    private fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val placeListFromRepository = repository.getPlaces()
            responseLiveData.postValue(
                PlaceListResponse.OnPlaceListFound(
                    placeListFromRepository
                )
            )
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(PlaceListResponse.OnError(error))
        }
    }
}
