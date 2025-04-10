package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.GetRecentPaymentsHistoryResponse
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.OnPaymentHistoryFilteredResponse
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.ServiceReactivated
import com.example.cleanarchitecture.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PaymentHistoryViewModel(val repository: IRepository) :
    BaseViewModel<PaymentHistoryUiState>() {
    companion object {
        const val LAST_PAYMENTS_ROW_LIMIT = 10
    }

    private val payments = MutableLiveData<List<Payment>>()

    private val pendingPayments = payments.asFlow().map { payments ->
        payments.filter { payment -> !payment.paid }
    }

    val reactivationButtonIsLoading = MutableLiveData(false)

    var subscriptionId: Int? = null

    fun getFilteredPaymentHistory(request: SearchPaymentsRequest) = executeWithProgress {
        val response = repository.getFilteredPaymentHistory(request)
        payments.value = response
        uiState.value = BaseUiState(OnPaymentHistoryFilteredResponse(response))
    }

    fun getLastPayments(itemsLimit: Int) = executeWithProgress {
        val response = repository.getRecentPaymentsHistory(subscriptionId!!, itemsLimit)
        payments.value = response
        uiState.value = BaseUiState(GetRecentPaymentsHistoryResponse(response))

    }

    fun showOnlyPendingPayments() = executeNoProgress {
        uiState.value = BaseUiState(OnPaymentHistoryFilteredResponse(pendingPayments.first()))
    }

    fun showAllPayments() = executeNoProgress {
        uiState.value = BaseUiState(OnPaymentHistoryFilteredResponse(payments.value!!))
    }

    fun reactivateService() =
        executeNoProgress(doBefore = { reactivationButtonIsLoading.value = true }, doFinally = {
            reactivationButtonIsLoading.value = false
        }) {
            subscriptionId?.let {
                repository.reactivateService(subscriptionId!!, repository.getUserSession()!!.id!!)
                uiState.value = BaseUiState(ServiceReactivated)
            }
        }
}
