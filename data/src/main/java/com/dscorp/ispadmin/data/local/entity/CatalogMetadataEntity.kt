package com.dscorp.ispadmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_metadata")
data class CatalogMetadataEntity(
    @PrimaryKey val catalogKey: String,
    val lastSyncedAt: Long
)
