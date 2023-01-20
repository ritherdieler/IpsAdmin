package com.dscorp.ispadmin.presentation.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.payment.PaymentUiState.*
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class ConsultPaymentsViewModel:ViewModel() {

    val repository: IRepository by inject(IRepository::class.java)

    val resultLiveData = MutableLiveData<PaymentUiState>()
    val formErrorLiveData = MutableLiveData<PaymentErrorUiState>()

    fun consultPayments(request: SearchPaymentsRequest) = viewModelScope.launch {
        try {
            resultLiveData.postValue(OnPaymentResponse(repository.consultPayments(request)))
        } catch (e: Exception) {
            resultLiveData.postValue(OnError(e.message))
        }
    }


}