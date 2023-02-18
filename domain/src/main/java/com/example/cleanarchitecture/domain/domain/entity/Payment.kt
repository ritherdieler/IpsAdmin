package com.example.cleanarchitecture.domain.domain.entity

import java.text.SimpleDateFormat
import java.util.Date

data class Payment(
    var id: Int? = null,
    var amountPaid: Double = 0.0,
    val discountAmount: Double = 0.0,
    val discountReason: String = "",
    var paymentDate: Long = 0,
    var billingDate: Long = 0,
    var method: String = "",
    var paid: Boolean = false,
    var amountToPay: Double = 0.0,
):java.io.Serializable
{
    fun paymentDateStr(): String {
        val date = Date(paymentDate)
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    fun billingDateStr(): String {
        val date = Date(billingDate)
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    fun amountPaidStr(): String {
        return "S/$amountPaid"
    }

    fun amountToPayStr(): String {
        return "S/$amountToPay"
    }

    fun paidStatusStr(): String {
        return if (paid) "Pagado" else "Pendiente"
    }
    fun discountAmountStr(): String {
        return "S/$discountAmount"
    }

}
