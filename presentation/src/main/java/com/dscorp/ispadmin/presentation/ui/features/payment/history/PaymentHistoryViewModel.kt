package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.*
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class PaymentHistoryViewModel(val repository: IRepository) : BaseViewModel<PaymentHistoryUiState>() {

    companion object {
        const val LAST_PAYMENTS_LIMIT = 10
    }

    private val payments = MutableLiveData<List<Payment>>()

    private val pendingPayments = payments.asFlow().map { payments ->
        payments.filter { payment -> !payment.paid }
    }

    fun getFilteredPaymentHistory(request: SearchPaymentsRequest) = executeWithProgress{
            val response = repository.getFilteredPaymentHistory(request)
            payments.value = response
            uiState.value = BaseUiState( OnPaymentHistoryFilteredResponse(response))
    }

    fun getLastPayments(idSubscription: Int, itemsLimit: Int) = executeWithProgress {
            val response = repository.getRecentPaymentsHistory(idSubscription, itemsLimit)
            payments.value = response
            uiState.value = BaseUiState(GetRecentPaymentsHistoryResponse(response))

    }

     fun showOnlyPendingPayments()  = executeNoProgress{
        uiState.value = BaseUiState(OnPaymentHistoryFilteredResponse(pendingPayments.first()))
    }

    fun showAllPayments() = executeNoProgress {
        uiState.value = BaseUiState(OnPaymentHistoryFilteredResponse(payments.value!!))
    }
}
