package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import android.app.Service
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.napbox.edit.EditNapBoxFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.napbox.edit.EditNapBoxUiState
import com.dscorp.ispadmin.presentation.ui.features.serviceorder.editar.EditServiceOrderFormErrorUiState
import com.dscorp.ispadmin.presentation.ui.features.serviceorder.editar.EditServiceOrderUiState
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch

class RegisterServiceOrderViewModel(private val repository: IRepository) : ViewModel() {

    val editServiceOrderUiState = MutableLiveData<EditServiceOrderUiState>()
    val editFormErrorLiveData = MutableLiveData<EditServiceOrderFormErrorUiState>()

    val user = repository.getUserSession()
    val uiState = MutableLiveData<RegisterServiceOrderUiState>()
    val formErrorLiveData = MutableLiveData<RegisterServiceOrderFormError>()
    var subscription: SubscriptionResponse? = null
    var serviceOrder: ServiceOrderResponse? = null
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

    fun editServiceOrder(serviceOrder: ServiceOrder) = viewModelScope.launch {
        try {
            if (!editFormIsValid(serviceOrder)) return@launch
            val response = repository.editServiceOrder(serviceOrder)
            editServiceOrderUiState.postValue(
                EditServiceOrderUiState.ServiceOrderEditSuccessOrder(
                    response
                )
            )
        } catch (e: Exception) {
            editServiceOrderUiState.postValue(EditServiceOrderUiState.ServiceOrderEditErrorOrder(e.message))
        }
    }

    private fun formIsValid(serviceOrder: ServiceOrder): Boolean {

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
        if (serviceOrder.priority == 0) {
            formErrorLiveData.value = RegisterServiceOrderFormError.GenericError()
            return false
        }

        return true
    }

    private fun editFormIsValid(serviceOrder: ServiceOrder): Boolean {

        if (serviceOrder.issue.isEmpty()) {
            editFormErrorLiveData.value = EditServiceOrderFormErrorUiState.OnEtIssueError()
            return false
        }

        if (serviceOrder.subscriptionId == null) {
            editFormErrorLiveData.value = EditServiceOrderFormErrorUiState.OnSubscriptionError()
            return false
        }
        if (serviceOrder.userId == null) {
            editFormErrorLiveData.value = EditServiceOrderFormErrorUiState.GenericError()
            return false
        }
        if (serviceOrder.priority == 0) {
            editFormErrorLiveData.value = EditServiceOrderFormErrorUiState.GenericError()
            return false
        }


        return true
    }
}
