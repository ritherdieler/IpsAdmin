package com.dscorp.ispadmin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dscorp.ispadmin.data.local.entity.PendingSubscriptionEntity
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingSubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PendingSubscriptionEntity)

    @Query("SELECT * FROM pending_subscriptions ORDER BY createdAt ASC")
    suspend fun getAllFifo(): List<PendingSubscriptionEntity>

    @Query("SELECT * FROM pending_subscriptions WHERE localId = :localId")
    suspend fun getByLocalId(localId: String): PendingSubscriptionEntity?

    @Query("DELETE FROM pending_subscriptions WHERE localId = :localId")
    suspend fun deleteByLocalId(localId: String)

    @Query("SELECT * FROM pending_subscriptions ORDER BY createdAt ASC")
    fun observeAllFifo(): Flow<List<PendingSubscriptionEntity>>

    @Query("UPDATE pending_subscriptions SET status = :status WHERE localId = :localId")
    suspend fun updateStatus(localId: String, status: PendingSubscriptionStatus)

    @Query(
        """
        UPDATE pending_subscriptions
        SET status = :status, lastError = :lastError, retryCount = retryCount + 1
        WHERE localId = :localId
        """
    )
    suspend fun markFailed(localId: String, status: PendingSubscriptionStatus, lastError: String)

    @Query(
        """
        UPDATE pending_subscriptions
        SET status = :status, lastError = :lastError
        WHERE localId = :localId
        """
    )
    suspend fun markConflict(localId: String, status: PendingSubscriptionStatus, lastError: String)

    @Query(
        """
        UPDATE pending_subscriptions
        SET status = :status, lastError = :lastError, retryCount = retryCount + 1
        WHERE localId = :localId
        """
    )
    suspend fun markRetryableError(localId: String, status: PendingSubscriptionStatus, lastError: String)
}
