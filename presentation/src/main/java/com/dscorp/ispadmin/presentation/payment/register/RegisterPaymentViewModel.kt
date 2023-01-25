package com.dscorp.ispadmin.presentation.payment.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterPaymentViewModel : ViewModel(),KoinComponent {

    val registerPaymentUiState = MutableLiveData<RegisterPaymentUiState>()
    val repository :IRepository by inject()

    fun registerPayment(payment: Payment) = viewModelScope.launch {
        try {
            val response = repository.registerPayment()
            registerPaymentUiState.postValue(RegisterPaymentUiState.OnPaymentRegistered(response))
        } catch (e: Exception) {
            e.printStackTrace()
            //registerPaymentUiState.postValue(RegisterPaymentUiState.OnError(e.message))
        }
    }

}