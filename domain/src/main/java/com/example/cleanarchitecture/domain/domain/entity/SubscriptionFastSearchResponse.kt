package com.example.cleanarchitecture.domain.domain.entity

data class SubscriptionFastSearchResponse(
    var id: Int,
    var fullName: String,
){
    override fun toString(): String {
        return fullName
    }
}