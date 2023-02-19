package com.dscorp.ispadmin.presentation.ui.features.technicianslist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TechniciansListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<TechniciansListResponse>()

    init {
        initGetSubscriptions()
    }

    private fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val techniciansListFromRepository = repository.getTechnicians()
            responseLiveData.postValue(
                TechniciansListResponse.OnTechniciansListFound(
                    techniciansListFromRepository
                )
            )
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(TechniciansListResponse.OnError(error))
        }
    }
}
