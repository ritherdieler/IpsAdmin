package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 23/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class Plan(

    val id: String? =null,
    val name:String,
    val price: String,
    val downloadSpeed: String,
    val uploadSpeed: String,

    )
{
    override fun toString(): String {
        return name
    }
}