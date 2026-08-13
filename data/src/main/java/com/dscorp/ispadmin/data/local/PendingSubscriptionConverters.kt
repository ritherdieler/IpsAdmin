package com.dscorp.ispadmin.data.local

import androidx.room.TypeConverter
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus

class PendingSubscriptionConverters {
    @TypeConverter
    fun fromStatus(status: PendingSubscriptionStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): PendingSubscriptionStatus = PendingSubscriptionStatus.valueOf(value)
}
