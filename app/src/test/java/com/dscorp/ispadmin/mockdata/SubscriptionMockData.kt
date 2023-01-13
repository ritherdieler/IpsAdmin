package com.dscorp.ispadmin.mockdata

import com.dscorp.ispadmin.domain.entity.Subscription

/**
 * Created by Sergio Carrillo Diestra on 21/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/

val subscriptionListMock = listOf(
    Subscription(
    id = "123",
    code = "123",
    firstName = "Sergio",
    lastName = "Carrillo",
    dni = "48271836",
    password = "123456",
    address = "asdasd",
    phone = "123123123",
    subscriptionDate = 124124,
    isNew = true,
    serviceIsSuspended = true,
    planId = "1",
    networkDeviceId = "1",
    placeId = "1")
)