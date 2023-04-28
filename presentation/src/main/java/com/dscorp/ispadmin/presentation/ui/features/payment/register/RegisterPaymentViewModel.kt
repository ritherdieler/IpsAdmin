package com.dscorp.ispadmin.presentation.ui.features.payment.register

import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.ReactiveFormField
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.data2.data.repository.IRepository
import org.koin.core.component.KoinComponent

class RegisterPaymentViewModel(private val repository: IRepository) :
    BaseViewModel<RegisterPaymentUiState>(), KoinComponent {

    private val user = repository.getUserSession()
    var payment: Payment? = null
    val discountAmountField = ReactiveFormField<String?>(
        hintResourceId = R.string.discount,
        validator = { it != null && it.toDouble() >= 0 }
    )

    val discountReasonField = ReactiveFormField<String?>(
        hintResourceId = R.string.discount_reason,
    )

    val paymentMethodField = ReactiveFormField<String?>(
        hintResourceId = R.string.payment_method,
    )

    fun registerPayment() = executeWithProgress {
        if (!formIsValid()) return@executeWithProgress
        payment?.let {
            val registerPayment = Payment(
                id = payment!!.id,
                amountPaid = payment!!.amountToPay,
                discountAmount = discountAmountField.getValue()!!.toDouble(),
                discountReason = discountReasonField.getValue()!!,
                method = paymentMethodField.getValue()!!,
            )
            val response = repository.registerPayment(registerPayment)
            uiState.value = BaseUiState(RegisterPaymentUiState.OnPaymentRegistered(response))
        }
    }

    fun formIsValid(): Boolean =
        listOf(discountAmountField, discountReasonField, paymentMethodField).all { it.isValid }

}
