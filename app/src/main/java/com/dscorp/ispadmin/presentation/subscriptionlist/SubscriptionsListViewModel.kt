package com.dscorp.ispadmin.presentation.subscriptionlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListResponse.*
import com.dscorp.ispadmin.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsListViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val responseLiveData = MutableLiveData<SubscriptionsListResponse>()

    init {
        getSubscriptions()
    }

    private fun getSubscriptions() {
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