package com.example.cleanarchitecture.domain.domain.entity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Payment(
    var id: Int? = null,
    var amountPaid: Double = 0.0,
    val discountAmount: Double? = 0.0,
    val discountReason: String? = null,
    var paymentDate: Long = 0,
    var billingDate: Long = 0,
    var method: String = "",
    var paid: Boolean = false,
    var amountToPay: Double = 0.0,
    var responsibleId: Int = 0,
    var service: String? = null,
    var responsibleName: String? = null,
) : java.io.Serializable {

    //first letter of month in uppercase


    private val format = SimpleDateFormat("MMMM yyyy", java.util.Locale("es", "ES"))


    fun paymentDateStr(): String {
        val date = Date(paymentDate)
        return format.format(date).uppercase(Locale.ROOT)
    }

    fun billingDateStr(): String {
        val date = Date(billingDate)
        return format.format(date).uppercase(Locale.ROOT)
    }

    fun amountToPayStr(): String {
        return "S/.$amountToPay"
    }

    fun paidStatusStr(): String {
        return if (paid) "Pagado" else "Pendiente"
    }

    fun discountAmountStr(): String {
        return when {
            discountAmount == null -> ""
            discountAmount > 0.0 -> "S/.${discountAmount}"
            else -> ""
        }
    }
}
