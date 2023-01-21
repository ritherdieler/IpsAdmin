package com.dscorp.ispadmin.presentation.subscriptionfinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FindSubscriptionViewModel : ViewModel(), KoinComponent {
    val repository: IRepository by inject()

    val uiStateLiveData = MutableLiveData<FindSubscriptionUiState>()
    val uiErrorStateLiveData = MutableLiveData<FindSubscriptionUiErrorState>()

    fun findSubscription(dni: Int) = viewModelScope.launch {
        try {
            val response = repository.findSubscription(dni)
            uiStateLiveData.postValue(FindSubscriptionUiState.OnSubscriptionFound(response))
        } catch (e: Exception) {
            e.printStackTrace()
            uiErrorStateLiveData.postValue(FindSubscriptionUiErrorState.OnError(e.message))
        }
    }


}