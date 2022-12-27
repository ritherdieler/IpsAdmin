package com.dscorp.ispadmin.repository.model

/**
 * Created by Sergio Carrillo Diestra on 27/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class ServiceOrder(
    val id: String? = null,
    val latitude: Double,
    val longitude: Double,
    val createDate: Int,
    val attentionDate: Int,
)
