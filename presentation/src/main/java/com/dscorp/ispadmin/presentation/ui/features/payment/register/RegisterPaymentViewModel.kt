package com.dscorp.ispadmin.presentation.ui.features.payment.register

import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.extension.formIsValid
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.ReactiveFormField
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidDouble
import com.example.data2.data.repository.IRepository
import org.koin.core.component.KoinComponent

class RegisterPaymentViewModel(private val repository: IRepository) :
    BaseViewModel<RegisterPaymentUiState>(), KoinComponent {

    val user = repository.getUserSession()

    val discountAmountField = ReactiveFormField<String?>(
        hintResourceId = R.string.discount,
        errorResourceId = R.string.invalid_discount_amount,
        validator = { it?.isValidDouble() ?: true }
    )

    val discountReasonField = ReactiveFormField<String?>(
        hintResourceId = R.string.discount_reason,
        errorResourceId = R.string.must_select_payment_method,
    )

    var payment: Payment? = null
        set(value) {
            field = value
            val showDiscountFields = value?.paid ?: false
            discountAmountField.setValue(value?.discountAmount?.toString())
            discountReasonField.setValue(value?.discountReason)
            uiState.value = BaseUiState(RegisterPaymentUiState.ShowDiscountFields(showDiscountFields))
        }


    var registerButtonProgress = MutableLiveData(false)


    val paymentMethodField = ReactiveFormField<String?>(
        hintResourceId = R.string.payment_method,
        errorResourceId = R.string.must_select_payment_method,
        validator = { !it.isNullOrEmpty() }
    )

    fun registerPayment() = executeNoProgress(
        doBefore = { registerButtonProgress.value = true },
        doFinally = { registerButtonProgress.value = false }
    ) {
        if (!formIsValid()) return@executeNoProgress
        payment?.let {
            val registerPayment = Payment(
                id = payment!!.id,
                amountPaid = payment!!.amountToPay,
                discountAmount = discountAmountField.getValue()?.toDouble(),
                discountReason = discountReasonField.getValue(),
                method = paymentMethodField.getValue()!!,
                responsibleId = user?.id!!
            )
            val response = repository.registerPayment(registerPayment)
            uiState.value = BaseUiState(RegisterPaymentUiState.OnPaymentRegistered(response))
        }
    }

    fun formIsValid(): Boolean {
        val fields = listOf(paymentMethodField, discountAmountField, discountReasonField)
        return fields.formIsValid()
    }

}
