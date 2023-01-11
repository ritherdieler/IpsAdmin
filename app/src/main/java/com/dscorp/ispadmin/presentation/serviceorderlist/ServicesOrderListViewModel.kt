package com.dscorp.ispadmin.presentation.serviceorderlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.OnError
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.OnSubscriptionFound
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.Repository
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
            responseLiveData.postValue(ServicesOrderListResponse.OnServicesOrderListFound(servicesOrderListFromRepository))
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(ServicesOrderListResponse.OnError(error))
        }
    }
}