package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_places")
data class CatalogPlaceEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val latitude: Float?,
    val longitude: Float?
)
