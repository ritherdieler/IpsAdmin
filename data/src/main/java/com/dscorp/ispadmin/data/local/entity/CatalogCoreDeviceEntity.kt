package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_core_devices")
data class CatalogCoreDeviceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val password: String,
    val username: String,
    val ipAddress: String,
    val networkDeviceType: String?,
    val disabled: Boolean
)
