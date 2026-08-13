package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import com.dscorp.ispadmin.domain.storage.FacadePhotoStorage
import java.io.File
import java.util.UUID

class EnqueuePendingSubscriptionUseCase(
    private val pendingSubscriptionRepository: PendingSubscriptionRepository,
    private val facadePhotoStorage: FacadePhotoStorage,
    private val uuidGenerator: () -> String = { UUID.randomUUID().toString() },
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() }
) {
    suspend operator fun invoke(
        subscriptionJson: String,
        facadePhotoFile: File,
        installationOrderId: Int? = null
    ): Result<PendingSubscription> = runCatching {
        val localId = uuidGenerator()
        val clientRequestId = uuidGenerator()
        val savedPhoto = facadePhotoStorage.save(localId, facadePhotoFile)
        val pending = PendingSubscription(
            localId = localId,
            clientRequestId = clientRequestId,
            subscriptionJson = subscriptionJson,
            facadePhotoPath = savedPhoto.absolutePath,
            installationOrderId = installationOrderId,
            status = PendingSubscriptionStatus.PENDING,
            createdAt = currentTimeMillis()
        )
        pendingSubscriptionRepository.enqueue(pending).getOrThrow()
        pending
    }
}
