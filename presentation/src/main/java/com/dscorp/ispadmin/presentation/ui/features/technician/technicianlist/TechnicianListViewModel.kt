package com.dscorp.ispadmin.presentation.ui.features.technician.technicianlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TechnicianListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<TechnicianListResponse>()

    init {
        initGetSubscriptions()
    }

    private fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val napBoxesListFromRepository = repository.getTechnicians()
            responseLiveData.postValue(
                TechnicianListResponse.OnTechnicianListFound(
                    napBoxesListFromRepository
                )
            )
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(TechnicianListResponse.OnError(error))
        }
    }
}
