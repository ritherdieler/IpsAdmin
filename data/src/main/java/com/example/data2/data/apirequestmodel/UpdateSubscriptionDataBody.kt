package com.example.data2.data.apirequestmodel

import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse

data class UpdateSubscriptionDataBody(
    val subscriptionId: Int,
    val firstName: String,
    val lastName: String,
    val dni: String,
    val address: String,
    val phone: String,
    val placeId: String,
    val location: GeoLocation

)