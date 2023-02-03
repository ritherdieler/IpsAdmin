package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 27/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class ServiceOrder(

    val id: Int?=null,
    val latitude: Double? = null,
    val longitude: Double?=null,
    val issue: String,
    val subscriptionId: Int?=null,
)
