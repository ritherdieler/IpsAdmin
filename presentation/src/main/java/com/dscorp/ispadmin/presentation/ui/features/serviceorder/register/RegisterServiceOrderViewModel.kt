package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch

class RegisterServiceOrderViewModel(private val repository: IRepository) : ViewModel() {
    val user = repository.getUserSession()
    val uiState = MutableLiveData<RegisterServiceOrderUiState>()
    val formErrorLiveData = MutableLiveData<RegisterServiceOrderFormError>()
    var subscription: SubscriptionResponse? = null
    fun registerServiceOrder(serviceOrder: ServiceOrder) = viewModelScope.launch {
        serviceOrder.userId = user?.id

        if (!formIsValid(serviceOrder)) return@launch
        try {
            val response = repository.registerServiceOrder(serviceOrder)
            uiState.postValue(
                RegisterServiceOrderUiState.ServiceOrderRegisterSuccessOrder(response)
            )
        } catch (error: Exception) {
            error.printStackTrace()
            uiState.postValue(RegisterServiceOrderUiState.ServiceOrderRegisterErrorOrder(error))
        }
    }

    private fun formIsValid(serviceOrder: ServiceOrder): Boolean {

        if (serviceOrder.latitude == null || serviceOrder.longitude == null) {
            formErrorLiveData.value = RegisterServiceOrderFormError.OnEtLocationError()
            return false
        }

        if (serviceOrder.issue.isEmpty()) {
            formErrorLiveData.value = RegisterServiceOrderFormError.OnEtIssueError()
            return false
        }

        if (serviceOrder.subscriptionId == null) {
            formErrorLiveData.value = RegisterServiceOrderFormError.OnSubscriptionError()
            return false
        }
        if (serviceOrder.userId == null) {
            formErrorLiveData.value = RegisterServiceOrderFormError.GenericError()
            return false
        }
        if (serviceOrder.priority==0) {
            formErrorLiveData.value = RegisterServiceOrderFormError.GenericError()
            return false
        }

        return true
    }
}
