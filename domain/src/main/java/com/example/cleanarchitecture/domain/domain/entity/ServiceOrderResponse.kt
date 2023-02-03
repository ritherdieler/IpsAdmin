package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 27/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class ServiceOrderResponse(
    val id: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val issue: String? = null,
    val subscriptionId: Int? = null,
):java.io.Serializable
