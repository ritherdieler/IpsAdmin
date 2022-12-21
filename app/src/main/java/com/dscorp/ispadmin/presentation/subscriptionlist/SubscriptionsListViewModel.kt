package com.dscorp.ispadmin.presentation.subscriptionlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.OnError
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.OnSubscriptionFound
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.Repository
import kotlinx.coroutines.launch

class SubscriptionsListViewModel(private val repository: IRepository) : ViewModel() {
    val responseLiveData = MutableLiveData<SubscriptionsListResponse>()

    init {
        getSubscriptions()
    }

     fun getSubscriptions() {
        viewModelScope.launch {
            try {
                val subscriptionsListFromRepository = repository.getSubscriptions()
                responseLiveData.postValue(OnSubscriptionFound(subscriptionsListFromRepository))
            } catch (error: Exception) {
                responseLiveData.postValue(OnError(error))
            }
        }
    }
}