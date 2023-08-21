package com.example.cleanarchitecture.domain.domain.entity

class DashBoardDataResponse(
    var grossRevenue: Double = 0.0,
    var totalRaised: Double = 0.0,
    var totalDiscount: Double = 0.0,
    var totalToCollect: Double = 0.0,
    val installationResume: InstallationResume,
    val canceledSubscriptions: Int
) {
    fun grossRevenueAsString() = "$grossRevenue"

    fun totalRaisedAsString() = "$totalRaised"

    fun totalDiscountAsString() = "$totalDiscount"

    fun totalToCollectAsString() = "$totalToCollect"
    fun canceledSubscriptionsAsString() = "$canceledSubscriptions"
}