package com.dscorp.ispadmin.presentation.payment.register

sealed class RegisterPaymentErrorUiState(val message: String) {


    companion object {
        const val ERROR_INVALID_AMOUNT = "El monto debe ser mayor a 0"
        const val ERROR_INVALID_DISCOUNT = "El descuento no puede ser menor a 0 ni mayor al precio del plan"
        const val ERROR_INVALID_METHOD = "El método de pago no puede estar vacío"
        const val ERROR_GENERIC = "Ocurrio un error, contacte con soporte tecnico"
        const val ERROR_AMOUNT_PAID_LESS_THAN_PLAN_PRICE = "El monto pagado no puede ser menor al precio del plan"
        const val ERROR_AMOUNT_PAID_GREATER_THAN_PLAN_PRICE = "El monto pagado no puede ser mayor al precio del plan"
    }

    object InvalidAmountError : RegisterPaymentErrorUiState(ERROR_INVALID_AMOUNT)
    object InvalidDiscountError : RegisterPaymentErrorUiState(ERROR_INVALID_DISCOUNT)
    object InvalidMethodError : RegisterPaymentErrorUiState(ERROR_INVALID_METHOD)
    object GenericError : RegisterPaymentErrorUiState(ERROR_GENERIC)
    object AmountPaidLessThanPlanPriceError : RegisterPaymentErrorUiState(ERROR_AMOUNT_PAID_LESS_THAN_PLAN_PRICE)
    data class AmountPaidGreaterThanPlanPriceError(val netPayment: Float) : RegisterPaymentErrorUiState(ERROR_AMOUNT_PAID_GREATER_THAN_PLAN_PRICE)


}
