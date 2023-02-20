package com.example.cleanarchitecture.domain.domain.entity

class DashBoardDataResponse(
    var grossRevenue: Double = 0.0,
    var totalRaised: Double = 0.0,
    var totalDiscount: Double = 0.0,
    var totalToCollect: Double = 0.0
) {
    fun grossRevenueAsString() = "Total bruto S/.$grossRevenue"

    fun totalRaisedAsString() = " Recaudado - S/.$totalRaised"

    fun totalDiscountAsString() = "Descontado - S/.$totalDiscount"

    fun totalToCollectAsString() = "Por cobrar - S/.$totalToCollect"
}