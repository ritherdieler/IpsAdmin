package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_nap_boxes")
data class CatalogNapBoxEntity(
    @PrimaryKey val id: String,
    val code: String,
    val address: String,
    val mufaId: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val portsNumber: Int?,
    val placeName: String,
    val placeId: Int
)
