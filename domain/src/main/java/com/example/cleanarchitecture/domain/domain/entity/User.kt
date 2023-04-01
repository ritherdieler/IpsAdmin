package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class User(
    val id: Int? = null,
    val name: String,
    val lastName: String,
    val type: UserType? = null,
    val username: String,
    var password: String,
    val verified: Boolean,
){
    enum class UserType {
        ADMIN, TECHNICIAN, CLIENT, LOGISTIC, SALES, SECRETARY
    }

}