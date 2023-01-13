package com.dscorp.ispadmin.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 24/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class NetworkDevice(
        val id:String? = null,
        val name:String,
        val Password:String,
        val username:String,
        val ipAddress:String,
        )
{
        override fun toString(): String {
                return name
        }
}

