package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class RegisterServiceOrderViewModel : ViewModel() {
    private val repository: IRepository by inject(IRepository::class.java)

    val serviceOrderResponseLiveData = MutableLiveData<RegisterServiceOrderResponse>()
    val formErrorLiveData = MutableLiveData<RegisterServiceOrderFormError>()
    var subscription: SubscriptionResponse? = null
    fun registerServiceOrder(serviceOrder: ServiceOrder) = viewModelScope.launch {
        if (!formIsValid(serviceOrder)) return@launch
        try {
            val response = repository.registerServiceOrder(serviceOrder)
            serviceOrderResponseLiveData.postValue(
                RegisterServiceOrderResponse.ServiceOrderRegisterSuccess(response)
            )
        } catch (error: Exception) {
            error.printStackTrace()
            serviceOrderResponseLiveData.postValue(RegisterServiceOrderResponse.OnError(error))
        }
    }

    private fun formIsValid(serviceOrder: ServiceOrder): Boolean {

        if (serviceOrder.latitude == null || serviceOrder.longitude == null) {
            formErrorLiveData.postValue(RegisterServiceOrderFormError.OnEtLocationError())
            return false
        }

        if (serviceOrder.issue.isNullOrEmpty()) {
            formErrorLiveData.postValue(RegisterServiceOrderFormError.OnEtIssueError())
            return false
        }

        if (serviceOrder.subscriptionId == null) {
            formErrorLiveData.postValue(RegisterServiceOrderFormError.OnSubscriptionError())
            return false
        }

        return true
    }
}