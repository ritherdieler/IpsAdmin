package com.dscorp.ispadmin.domain.repository

import com.dscorp.ispadmin.domain.model.PendingSubscription
import kotlinx.coroutines.flow.Flow

interface PendingSubscriptionRepository {
    suspend fun enqueue(pending: PendingSubscription): Result<Unit>
    suspend fun getPendingFifo(): List<PendingSubscription>
    suspend fun getByLocalId(localId: String): PendingSubscription?
    suspend fun deleteByLocalId(localId: String): Result<Unit>
    fun observePending(): Flow<List<PendingSubscription>>
    suspend fun markSyncing(localId: String): Result<Unit>
    suspend fun markFailed(localId: String, error: String): Result<Unit>
    suspend fun markConflict(localId: String, error: String): Result<Unit>
    suspend fun markRetryableError(localId: String, error: String): Result<Unit>
}
