package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class User (

    val id:Int,
    val name:String,
    val lastName:String,
    val type:Int,
    val username:String,
    val password:String,
    val verified:Boolean,


        )