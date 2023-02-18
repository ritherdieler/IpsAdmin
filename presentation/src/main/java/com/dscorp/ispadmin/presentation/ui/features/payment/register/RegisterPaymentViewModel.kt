package com.dscorp.ispadmin.presentation.ui.features.payment.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterPaymentViewModel : ViewModel(), KoinComponent {

    val registerPaymentState = MutableLiveData<RegisterPaymentUiState>()
    val registerPaymentFormErrorState = MutableLiveData<RegisterPaymentErrorUiState>()

    val repository: IRepository by inject()

    fun registerPayment(payment: Payment) = viewModelScope.launch {
        if (paymentIsValid(payment))
            try {
                val response = repository.registerPayment(payment)
                registerPaymentState.postValue(RegisterPaymentUiState.OnPaymentRegistered(response))
            } catch (e: Exception) {
                e.printStackTrace()
                registerPaymentState.postValue(RegisterPaymentUiState.OnError(e.message))
            }
    }

    private fun paymentIsValid(payment: Payment): Boolean {
        if (payment.amountPaid <= 0) {
            registerPaymentFormErrorState.postValue(RegisterPaymentErrorUiState.InvalidAmountError)
            return false
        }
        if (payment.discountAmount < 0) {
            registerPaymentFormErrorState.postValue(RegisterPaymentErrorUiState.InvalidDiscountError)
            return false
        }
        if (payment.method.isEmpty()) {
            registerPaymentFormErrorState.postValue(RegisterPaymentErrorUiState.InvalidMethodError)
            return false
        }
        if (payment.discountAmount > (payment.amountToPay)) {
            registerPaymentFormErrorState.postValue(RegisterPaymentErrorUiState.InvalidDiscountError)
            return false
        }
        if(payment.id == null || payment.id!! <= 0){
            registerPaymentFormErrorState.postValue(RegisterPaymentErrorUiState.GenericError)
            return false
        }
        return true
    }

}