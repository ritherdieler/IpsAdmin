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

    val resultLiveData = MutableLiveData<PaymentHistoryUiState>()
    val formErrorLiveData = MutableLiveData<PaymentHistoryErrorUiState>()

    fun getPaymentHistory(request: SearchPaymentsRequest) = viewModelScope.launch {
        try {
            val response = repository.getPaymentHistory(request)
            resultLiveData.postValue(OnPaymentResponseHistory(response))
        } catch (e: Exception) {
            resultLiveData.postValue(OnError(e.message))
        }
    }


}