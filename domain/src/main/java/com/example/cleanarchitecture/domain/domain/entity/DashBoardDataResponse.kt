package com.example.cleanarchitecture.domain.domain.entity

class DashBoardDataResponse(
    var grossRevenue: Double = 0.0,
    var totalRaised: Double = 0.0,
    var totalDiscount: Double = 0.0,
    var totalToCollect: Double = 0.0,
    val installationResume: InstallationResume,
    val canceledSubscriptions: Int
) {
    fun grossRevenueAsString() = "Total bruto S/.$grossRevenue"

    fun totalRaisedAsString() = " Recaudado S/.$totalRaised"

    fun totalDiscountAsString() = "Descontado S/.$totalDiscount"

    fun totalToCollectAsString() = "Por cobrar S/.$totalToCollect"
    fun canceledSubscriptionsAsString() = "Subs. canceladas $canceledSubscriptions"
}