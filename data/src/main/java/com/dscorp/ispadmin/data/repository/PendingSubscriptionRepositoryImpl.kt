package com.dscorp.ispadmin.data.repository

import com.dscorp.ispadmin.data.local.dao.PendingSubscriptionDao
import com.dscorp.ispadmin.data.local.mapper.toDomain
import com.dscorp.ispadmin.data.local.mapper.toEntity
import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PendingSubscriptionRepositoryImpl(
    private val pendingSubscriptionDao: PendingSubscriptionDao
) : PendingSubscriptionRepository {

    override suspend fun enqueue(pending: PendingSubscription): Result<Unit> = runCatching {
        pendingSubscriptionDao.insert(pending.toEntity())
    }

    override suspend fun getPendingFifo(): List<PendingSubscription> {
        return pendingSubscriptionDao.getAllFifo().map { it.toDomain() }
    }

    override suspend fun getByLocalId(localId: String): PendingSubscription? {
        return pendingSubscriptionDao.getByLocalId(localId)?.toDomain()
    }

    override suspend fun deleteByLocalId(localId: String): Result<Unit> = runCatching {
        pendingSubscriptionDao.deleteByLocalId(localId)
    }

    override fun observePending(): Flow<List<PendingSubscription>> {
        return pendingSubscriptionDao.observeAllFifo().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun markSyncing(localId: String): Result<Unit> = runCatching {
        pendingSubscriptionDao.updateStatus(localId, PendingSubscriptionStatus.SYNCING)
    }

    override suspend fun markFailed(localId: String, error: String): Result<Unit> = runCatching {
        pendingSubscriptionDao.markFailed(
            localId = localId,
            status = PendingSubscriptionStatus.FAILED,
            lastError = error
        )
    }

    override suspend fun markConflict(localId: String, error: String): Result<Unit> = runCatching {
        pendingSubscriptionDao.markConflict(
            localId = localId,
            status = PendingSubscriptionStatus.CONFLICT,
            lastError = error
        )
    }

    override suspend fun markRetryableError(localId: String, error: String): Result<Unit> = runCatching {
        pendingSubscriptionDao.markRetryableError(
            localId = localId,
            status = PendingSubscriptionStatus.PENDING,
            lastError = error
        )
    }
}
