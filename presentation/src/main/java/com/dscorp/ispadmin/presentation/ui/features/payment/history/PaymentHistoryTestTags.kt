package com.dscorp.ispadmin.presentation.ui.features.payment.history

object PaymentHistoryTestTags {
    const val FILTER_PENDING = "payment_history_filter_pending"
    const val RESTORE_CONNECTION = "payment_history_restore_connection"
    const val REACTIVATION_NOTES = "payment_history_reactivation_notes"
    const val REACTIVATE_SUBMIT = "payment_history_reactivate_submit"

    fun paymentItem(paymentId: Int) = "payment_history_item_$paymentId"

    val interactive = listOf(
        FILTER_PENDING,
        RESTORE_CONNECTION,
        REACTIVATION_NOTES,
        REACTIVATE_SUBMIT
    )
}

object PaymentHistoryContentDescriptions {
    const val PAYMENT_METHOD_ICON = "Método de pago"
    const val RESPONSIBLE_ICON = "Responsable del pago"
}
