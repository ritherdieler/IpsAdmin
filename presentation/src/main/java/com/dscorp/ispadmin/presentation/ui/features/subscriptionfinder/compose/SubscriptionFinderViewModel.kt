package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.example.cleanarchitecture.domain.entity.CustomerData
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.ServiceStatus
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val REQUEST_DELAY = 500L

class SubscriptionFinderViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val subscriptionsFlow = MutableStateFlow<List<SubscriptionResume>>(emptyList())
    val subscriptionFlowGrouped: Flow<Map<ServiceStatus, List<SubscriptionResume>>> =
        subscriptionsFlow.map { list ->
            list.map {
                if (it.serviceStatus != ServiceStatus.CANCELLED) it.copy(serviceStatus = ServiceStatus.ACTIVE)
                else it.copy(serviceStatus = ServiceStatus.CANCELLED)
            }.groupBy { it.serviceStatus }
        }

    val documentNumberFlow = MutableSharedFlow<SubscriptionFilter>(extraBufferCapacity = 1)
    val updateCustomerDataFlow =
        MutableStateFlow<SaveSubscriptionState>(SaveSubscriptionState.Success)
    val cancelSubscriptionFlow =
        MutableStateFlow<CancelSubscriptionState>(CancelSubscriptionState.Empty)

    val napBoxDialogDataFlow = MutableStateFlow<NapBoxesState>(NapBoxesState.Loading)

    init {
        findSubscription()
    }
    fun resetNapBoxFlow() {
        napBoxDialogDataFlow.value = NapBoxesState.Loading
    }

    @OptIn(FlowPreview::class)
    fun findSubscription() = viewModelScope.launch {
        documentNumberFlow.debounce(REQUEST_DELAY)
            .collect { filterType ->
                val response = when (filterType) {
                    is SubscriptionFilter.BY_DATE -> {
                        if (filterType.startDate.isEmpty() || filterType.endDate.isEmpty()) {
                            subscriptionsFlow.value = emptyList()
                            return@collect
                        }
                        repository.findSubscriptionBySubscriptionDate(
                            filterType.startDate,
                            filterType.endDate
                        )
                    }

                    is SubscriptionFilter.BY_DOCUMENT -> {
                        if (filterType.documentNumber.isEmpty()) {
                            subscriptionsFlow.value = emptyList()
                            return@collect
                        } else {
                            repository.findSubscriptionByDNI(filterType.documentNumber)
                        }
                    }

                    is SubscriptionFilter.BY_NAME -> {
                        if (filterType.name.isEmpty() && filterType.lastName.isEmpty()) {
                            subscriptionsFlow.value = emptyList()
                            return@collect
                        } else {
                            repository.findSubscriptionByNameAndLastName(
                                filterType.name,
                                filterType.lastName
                            )
                        }
                    }
                }
                subscriptionsFlow.value = response
            }

    }

    fun updateCustomerData(customer: CustomerData) = viewModelScope.launch {
        try {
            updateCustomerDataFlow.value = SaveSubscriptionState.Loading
            repository.updateCustomerData(customer)
            updateCustomerDataFlow.value = SaveSubscriptionState.Success
        } catch (e: Exception) {
            e.printStackTrace()
            updateCustomerDataFlow.value = SaveSubscriptionState.Error
        }
    }


    fun cancelSubscription(subscriptionId: Int) = viewModelScope.launch {
        try {
            cancelSubscriptionFlow.value = CancelSubscriptionState.Loading
            repository.cancelSubscription(subscriptionId)
            cancelSubscriptionFlow.value = CancelSubscriptionState.Success
        } catch (e: Exception) {
            e.printStackTrace()
            cancelSubscriptionFlow.value = CancelSubscriptionState.Error
        }
    }

    fun removeSubscriptionFromList(id: Int) {
        subscriptionsFlow.value = subscriptionsFlow.value.filter { it.id != id }
        cancelSubscriptionFlow.value = CancelSubscriptionState.Empty
    }

    fun getNapBoxes() = viewModelScope.launch {
        try {
            napBoxDialogDataFlow.value = NapBoxesState.Loading
            val response = repository.getNapBoxes()
            napBoxDialogDataFlow.value = NapBoxesState.NapBoxListLoaded(response)

        } catch (e: Exception) {
            napBoxDialogDataFlow.value = NapBoxesState.Error
            e.printStackTrace()
        }
    }

    fun changeNapBox(request: MoveOnuRequest) = viewModelScope.launch {
        try {
            napBoxDialogDataFlow.value = NapBoxesState.Loading
            repository.changeSubscriptionNapBox(request)
            napBoxDialogDataFlow.value = NapBoxesState.NapBoxChanged
        } catch (e: Exception) {
            napBoxDialogDataFlow.value = NapBoxesState.Error
            e.printStackTrace()
        }
    }

}


sealed class SaveSubscriptionState {
    object Loading : SaveSubscriptionState()
    object Success : SaveSubscriptionState()
    object Error : SaveSubscriptionState()
}

sealed class CancelSubscriptionState {
    object Empty : CancelSubscriptionState()
    object Loading : CancelSubscriptionState()
    object Success : CancelSubscriptionState()
    object Error : CancelSubscriptionState()
}

sealed class NapBoxesState {
    object Loading : NapBoxesState()
    data class NapBoxListLoaded(val items: List<NapBoxResponse>) : NapBoxesState()
    object NapBoxChanged : NapBoxesState()
    object Error : NapBoxesState()
}

