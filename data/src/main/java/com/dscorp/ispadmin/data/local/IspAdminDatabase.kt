package com.dscorp.ispadmin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dscorp.ispadmin.data.local.dao.CatalogDao
import com.dscorp.ispadmin.data.local.dao.PendingSubscriptionDao
import com.dscorp.ispadmin.data.local.entity.CatalogCoreDeviceEntity
import com.dscorp.ispadmin.data.local.entity.CatalogMetadataEntity
import com.dscorp.ispadmin.data.local.entity.CatalogNapBoxEntity
import com.dscorp.ispadmin.data.local.entity.CatalogOnuEntity
import com.dscorp.ispadmin.data.local.entity.CatalogPlaceEntity
import com.dscorp.ispadmin.data.local.entity.CatalogPlanEntity
import com.dscorp.ispadmin.data.local.entity.PendingSubscriptionEntity

@Database(
    entities = [
        CatalogPlanEntity::class,
        CatalogPlaceEntity::class,
        CatalogNapBoxEntity::class,
        CatalogOnuEntity::class,
        CatalogCoreDeviceEntity::class,
        CatalogMetadataEntity::class,
        PendingSubscriptionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PendingSubscriptionConverters::class)
abstract class IspAdminDatabase : RoomDatabase() {
    abstract fun pendingSubscriptionDao(): PendingSubscriptionDao
    abstract fun catalogDao(): CatalogDao
}
