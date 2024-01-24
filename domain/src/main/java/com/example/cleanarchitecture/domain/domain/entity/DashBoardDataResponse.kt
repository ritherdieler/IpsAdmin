package com.example.cleanarchitecture.domain.domain.entity

import java.text.NumberFormat
import java.util.Locale

class DashBoardDataResponse(
    var grossRevenue: Double = 0.0,
    var totalRaised: Double = 0.0,
    var totalDiscount: Double = 0.0,
    var totalToCollect: Double = 0.0,
    val installationResume: InstallationResume,
    val canceledSubscriptions: Int,
    val paymentResume: Map<String, Double>,
    val subscriptionsHistoryStatics: List<MonthlySubscriptionResume>,
    val monthlyCollects: List<MonthlyCollectsResume>,
    val grossRevenueHistoryStatics: List<MonthlyGrossRevenueResume>

) {
    fun grossRevenueAsString() = grossRevenue.toCurrencyString()

    fun totalRaisedAsString() = totalRaised.toCurrencyString()

    fun totalDiscountAsString() = totalDiscount.toCurrencyString()

    fun totalToCollectAsString() = totalToCollect.toCurrencyString()
    fun canceledSubscriptionsAsString() = "$canceledSubscriptions"

    fun Double.toCurrencyString(): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale("es", "PE"))
        return numberFormat.format(this)
    }
}

data class MonthlyGrossRevenueResume(
    val totalCharged: Double,
    val billingDate: Long
)


