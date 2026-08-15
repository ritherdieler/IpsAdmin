package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.connectivity.NetworkConnectivityMonitor
import com.dscorp.ispadmin.domain.exception.NoConnectivity
import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import com.dscorp.ispadmin.domain.repository.SubscriptionSyncOutcome
import com.dscorp.ispadmin.domain.repository.SubscriptionSyncRemote
import com.dscorp.ispadmin.domain.storage.FacadePhotoStorage

class SyncPendingSubscriptionsUseCase(
    private val connectivityMonitor: NetworkConnectivityMonitor,
    private val pendingSubscriptionRepository: PendingSubscriptionRepository,
    private val facadePhotoStorage: FacadePhotoStorage,
    private val subscriptionSyncRemote: SubscriptionSyncRemote
) {
    suspend operator fun invoke(): Result<PendingSubscriptionSyncResult> = runCatching {
        if (!connectivityMonitor.isConnected()) {
            throw NoConnectivity()
        }
        var syncedCount = 0
        var failedCount = 0
        var lastError: String? = null
        pendingSubscriptionRepository.getPendingFifo()
            .filter { it.isSyncable() }
            .forEach { pending ->
                when (val outcome = syncOne(pending)) {
                    SubscriptionSyncOutcome.Success -> syncedCount++
                    SubscriptionSyncOutcome.Conflict -> {
                        failedCount++
                        lastError = CONFLICT_ERROR
                    }
                    SubscriptionSyncOutcome.IpConflict -> {
                        failedCount++
                        lastError = IP_CONFLICT_ERROR
                    }
                    is SubscriptionSyncOutcome.Failure -> {
                        failedCount++
                        lastError = outcome.message
                    }
                }
            }
        PendingSubscriptionSyncResult(
            syncedCount = syncedCount,
            failedCount = failedCount,
            lastError = lastError
        )
    }

    private suspend fun syncOne(pending: PendingSubscription): SubscriptionSyncOutcome {
        pendingSubscriptionRepository.markSyncing(pending.localId).getOrThrow()
        val photoFile = facadePhotoStorage.fileFor(pending.localId)
        val outcome = subscriptionSyncRemote.uploadPending(
            subscriptionJson = pending.subscriptionJson,
            clientRequestId = pending.clientRequestId,
            installationOrderId = pending.installationOrderId,
            facadePhotoFile = photoFile.takeIf { it.exists() } ?: photoFile
        )
        when (outcome) {
            SubscriptionSyncOutcome.Success -> {
                pendingSubscriptionRepository.deleteByLocalId(pending.localId).getOrThrow()
                facadePhotoStorage.delete(pending.localId)
            }
            SubscriptionSyncOutcome.Conflict -> {
                pendingSubscriptionRepository.markConflict(
                    localId = pending.localId,
                    error = CONFLICT_ERROR
                ).getOrThrow()
            }
            SubscriptionSyncOutcome.IpConflict -> {
                pendingSubscriptionRepository.markConflict(
                    localId = pending.localId,
                    error = IP_CONFLICT_ERROR
                ).getOrThrow()
            }
            is SubscriptionSyncOutcome.Failure -> {
                pendingSubscriptionRepository.markRetryableError(
                    localId = pending.localId,
                    error = outcome.message
                ).getOrThrow()
            }
        }
        return outcome
    }

    private fun PendingSubscription.isSyncable(): Boolean {
        return status == PendingSubscriptionStatus.PENDING ||
            status == PendingSubscriptionStatus.SYNCING
    }

    private companion object {
        const val CONFLICT_ERROR = "HTTP 409: la suscripción ya existe en el servidor"
        const val IP_CONFLICT_ERROR =
            "IP ya en uso. Coordina otra IP con el equipo e intenta de nuevo."
    }
}
