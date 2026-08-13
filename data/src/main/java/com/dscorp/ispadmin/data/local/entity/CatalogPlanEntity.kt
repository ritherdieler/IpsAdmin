package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_plans")
data class CatalogPlanEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val price: Double?,
    val downloadSpeed: String?,
    val uploadSpeed: String?,
    val type: String?
)
