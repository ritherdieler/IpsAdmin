package com.dscorp.ispadmin.presentation.ui.features.payment.register

object RegisterPaymentTestTags {
    const val BACK = "payment_register_back"
    const val PAYMENT_METHOD = "payment_register_method"
    const val ELECTRONIC_PAYER_NAME = "payment_register_electronic_payer"
    const val DISCOUNT_TOGGLE = "payment_register_discount_toggle"
    const val DISCOUNT_AMOUNT = "payment_register_discount_amount"
    const val DISCOUNT_REASON = "payment_register_discount_reason"
    const val SUBMIT = "payment_register_submit"
    const val SUCCESS_DISMISS = "payment_register_success_dismiss"

    val interactive = listOf(
        BACK,
        PAYMENT_METHOD,
        ELECTRONIC_PAYER_NAME,
        DISCOUNT_TOGGLE,
        DISCOUNT_AMOUNT,
        DISCOUNT_REASON,
        SUBMIT,
        SUCCESS_DISMISS
    )
}

object RegisterPaymentContentDescriptions {
    const val DEBT_ICON = "Deuda a pagar"
    const val PAYER_ICON = "Datos del pagador"
    const val DISCOUNT_ICON = "Descuento"
    const val CONFIRM_ICON = "Confirmar pago"
    const val SUCCESS_ICON = "Pago registrado exitosamente"
}
