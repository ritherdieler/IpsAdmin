package com.dscorp.ispadmin.mockdata

import com.example.cleanarchitecture.domain.domain.entity.*

/**
 * Created by Sergio Carrillo Diestra on 21/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/

val subscriptionListMock = listOf(
    SubscriptionResponse(
        id = "123",
        code = "123",
        firstName = "Sergio",
        lastName = "Carrillo",
        dni = "48271836",
        password = "123456",
        address = "asdasd",
        phone = "123123123",
        serviceIsSuspended = true,
        location = GeoLocation(
            latitude = 123.123,
            longitude = 123.123
        ),
        napBox = NapBox(
            id = "123",
            code = "123",
            location = GeoLocation(
                latitude = 123.123,
                longitude = 123.123
            ),
            address = "asdasd"
        ),
        networkDevice = NetworkDevice(
            id = 1,
            name = "asdad",
            password = "1313",
            username = "1313",
            ipAddress = "1313",
        ),
        new = false,
        place = Place(
            id = "3",
            abbreviation = "asdsa",
            name = "la villa",
            location = GeoLocation(latitude = 123.123, longitude = 123.123)
        ),
        plan = Plan(
            id = "1",
            name = "asd",
            price = 23.0,
            downloadSpeed = "12",
            uploadSpeed = "123"
        ),
        technician = Technician(
            id = "1",
            firstName = "asd",
            lastName = "asd",
            dni = "123",
            phone = "123",
        ),

        )
)