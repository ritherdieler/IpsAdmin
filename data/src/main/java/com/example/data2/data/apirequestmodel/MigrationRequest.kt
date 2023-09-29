package com.example.data2.data.apirequestmodel

import com.example.cleanarchitecture.domain.domain.entity.Onu

data class MigrationRequest(
    val onu: Onu?,
    val planId: String?,
    var subscriptionId: Int?,
    val price: String?,
    val notes: String?
) {
    fun isValid() = onu != null && planId != null && subscriptionId != null
}