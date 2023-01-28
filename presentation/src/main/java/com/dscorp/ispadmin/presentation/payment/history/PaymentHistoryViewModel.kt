package com.dscorp.ispadmin.presentation.payment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.payment.history.PaymentHistoryUiState.*
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
            resultLiveData.postValue(OnPaymentResponseHistory(repository.getPaymentHistory(request)))
        } catch (e: Exception) {
            resultLiveData.postValue(OnError(e.message))
        }
    }


}