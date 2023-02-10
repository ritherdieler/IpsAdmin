package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.*
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class PaymentHistoryViewModel:ViewModel() {

    val repository: IRepository by inject(IRepository::class.java)

    val uiStateLiveData = MutableLiveData<PaymentHistoryUiState>()
    val formErrorLiveData = MutableLiveData<PaymentHistoryErrorUiState>()

    fun getFilteredPaymentHistory(request: SearchPaymentsRequest) = viewModelScope.launch {
        try {
            val response = repository.getFilteredPaymentHistory(request)
            uiStateLiveData.postValue(OnPaymentHistoryFilteredResponse(response))
        } catch (e: Exception) {
            uiStateLiveData.postValue(OnError(e.message))
        }
    }

    fun getLastPayments(idSubscription: Int, itemsLimit: Int) = viewModelScope.launch {
        try {
            val response = repository.getRecentPaymentsHistory(idSubscription,itemsLimit)
            uiStateLiveData.postValue(GetRecentPaymentsHistoryResponse(response))
        } catch (e: Exception) {
            uiStateLiveData.postValue(GetRecentPaymentHistoryError(e.message))
        }
    }

}