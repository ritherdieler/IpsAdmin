package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 27/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class ServiceOrderResponse(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val issue: String,
    val subscriptionId: Int,
):java.io.Serializable
