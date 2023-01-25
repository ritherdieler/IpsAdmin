package com.dscorp.ispadmin.presentation.payment.register

sealed class RegisterPaymentErrorUiState(val message: String) {
    companion object {
        const val ERROR_INVALID_AMOUNT = "El monto debe ser mayor a 0"
        const val ERROR_INVALID_DISCOUNT = "El descuento no puede ser menor a 0"
        const val ERROR_INVALID_METHOD = "El método de pago no puede estar vacío"
    }

    object InvalidAmountError : RegisterPaymentErrorUiState(ERROR_INVALID_AMOUNT)
    object InvalidDiscountError : RegisterPaymentErrorUiState(ERROR_INVALID_DISCOUNT)
    object InvalidMethodError : RegisterPaymentErrorUiState(ERROR_INVALID_METHOD)

}
