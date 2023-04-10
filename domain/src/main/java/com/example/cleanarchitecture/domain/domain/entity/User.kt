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
    val dni: String,
    val email: String,
    val phone: String,
    ) {
    enum class UserType(val value: String) {
        ADMIN("Administrador"),
        TECHNICIAN("Tecnico"),
        CLIENT("Cliente"),
        LOGISTIC("Logistica"),
        SALES("Ventas"),
        SECRETARY("Secretario")
    }

    fun typeAsString() = type?.value ?: ""

}