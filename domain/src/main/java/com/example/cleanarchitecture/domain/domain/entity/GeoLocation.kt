package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 15/01/2023.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class GeoLocation(latitude: Double, longitude: Double) {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    init {
        this.latitude = latitude
        this.longitude = longitude
    }
}