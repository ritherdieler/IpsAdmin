package com.dscorp.ispadmin.presentation.ui.features.payment.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.extension.formIsValid
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.ReactiveFormField
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.REQUEST_DELAY
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.cleanarchitecture.domain.domain.entity.extensions.PayerFinderResult
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidDouble
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


class RegisterPaymentViewModel(private val repository: IRepository) :
    BaseViewModel<RegisterPaymentUiState>(), KoinComponent {


    val subscriptionFlow = MutableStateFlow<List<PayerFinderResult>>(emptyList())

    val paymentSearchFlow = MutableStateFlow("")

    val user = repository.getUserSession()

    val discountAmountField = ReactiveFormField<String?>(
        hintResourceId = R.string.discount,
        errorResourceId = R.string.invalid_discount_amount,
        validator = {
            it?.isValidDouble() ?: true && (it?.toDouble() ?: 0.0) <= (payment?.amountToPay ?: 0.0)
        }
    )

    val discountReasonField = ReactiveFormField<String?>(
        hintResourceId = R.string.discount_reason,
        errorResourceId = R.string.must_select_payment_method,
    )

    var payment: Payment? = null
        set(value) {
            field = value
            // no esta pagado y tienes un monto pagado entonces es una factura por reconexion
            val showDiscountFields =
                (value?.amountPaid != null && value.amountPaid!! > 0) && value.paid.not()
            if (showDiscountFields) {
                discountAmountField.setValue(value!!.discountAmount?.toString())
                discountReasonField.setValue(value.discountReason)
                uiState.value =
                    BaseUiState(RegisterPaymentUiState.HideDiscountFields)
            }

        }

    var registerButtonProgress = MutableLiveData(false)

    val paymentMethodField = ReactiveFormField<String?>(
        hintResourceId = R.string.payment_method,
        errorResourceId = R.string.must_select_payment_method,
        validator = { !it.isNullOrEmpty() }
    )


    init {
        observeFinderField()
    }

    @OptIn(FlowPreview::class)
    private fun observeFinderField() = viewModelScope.launch {
        paymentSearchFlow.debounce(REQUEST_DELAY).collect {
            if (it.isEmpty().or(it.length < 3)) return@collect
            val response = repository.findPaymentByElectronicPayerName(it)
            subscriptionFlow.value = response
        }
    }


    fun getElectronicPayers() = executeNoProgress {
        val response = repository.getElectronicPayers(payment!!.subscriptionId!!)
        uiState.value = BaseUiState(RegisterPaymentUiState.OnElectronicPayersLoaded(response))
    }

    fun registerPayment() = executeNoProgress(
        doBefore = { registerButtonProgress.value = true },
        doFinally = { registerButtonProgress.value = false }
    ) {
        if (!formIsValid()) return@executeNoProgress
        payment?.let {
            val registerPayment = Payment(
                id = payment!!.id,
                discountAmount = try {
                    discountAmountField.getValue()?.toDouble()
                } catch (e: Exception) {
                    0.0
                },
                discountReason = discountReasonField.getValue(),
                method = paymentMethodField.getValue()!!,
                responsibleId = user?.id!!,
                electronicPayerName = payment!!.electronicPayerName
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
