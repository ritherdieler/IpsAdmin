package com.example.data2.data.apirequestmodel

data class SearchPaymentsRequest (
    var subscriptionCode: Int? = null,
    var startDate: Long? = null,
    var endDate: Long? = null
)