package com.dscorp.ispadmin.presentation.ui.features.serviceorder.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ServicesOrderListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<ServicesOrderListResponse>()

    init {
        initGetSubscriptions()
        
    }

    private fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val servicesOrderListFromRepository = repository.getServicesOrder()
            responseLiveData.postValue(
                ServicesOrderListResponse.OnServicesOrderListFound(
                    servicesOrderListFromRepository
                )
            )
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(ServicesOrderListResponse.OnError(error))
        }
    }
}