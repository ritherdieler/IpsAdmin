package com.example.data2.data.apirequestmodel

data class SearchPaymentsRequest (
    var subscriptionId: Int?= null,
    var startDate: Long? = null,
    var endDate: Long? = null
)