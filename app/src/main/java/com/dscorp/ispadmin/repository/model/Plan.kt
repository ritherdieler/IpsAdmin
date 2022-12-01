package com.dscorp.ispadmin.repository.model

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
    val price:Double,
    val downloadSpeed: Int,
    val uploadSpeed:Int,

    )
{
    override fun toString(): String {
        return name
    }
}