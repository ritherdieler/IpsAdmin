package com.dscorp.ispadmin.repository.model

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class User (

    val id:Int?=null,
    val name:String,
    val lastName:String,
    val type:Int,
    val username:String,
    val password:String,
    val verified:Boolean


        )