package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_onus")
data class CatalogOnuEntity(
    @PrimaryKey val sn: String,
    val board: String,
    val oltId: String,
    val onu: String,
    val onuTypeId: String,
    val onuTypeName: String,
    val ponType: String,
    val port: String
)
