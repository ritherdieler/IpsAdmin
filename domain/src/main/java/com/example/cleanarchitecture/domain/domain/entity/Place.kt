package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class Place(
    val id: String? = null,
    val abbreviation: String,
    val name: String,
) {
    override fun toString(): String {
        return name
    }
}

