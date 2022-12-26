package com.dscorp.ispadmin.presentation.subscriptionlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.OnError
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.OnSubscriptionFound
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.Repository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SubscriptionsListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<SubscriptionsListResponse>()

    init {
        initGetSubscriptions()
    }

    fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val subscriptionsListFromRepository = repository.getSubscriptions()
            responseLiveData.postValue(OnSubscriptionFound(subscriptionsListFromRepository))
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(OnError(error))
        }
    }

}