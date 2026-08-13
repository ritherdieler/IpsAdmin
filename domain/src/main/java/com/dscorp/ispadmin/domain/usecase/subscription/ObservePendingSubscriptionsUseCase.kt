package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import kotlinx.coroutines.flow.Flow

class ObservePendingSubscriptionsUseCase(
    private val pendingSubscriptionRepository: PendingSubscriptionRepository
) {
    operator fun invoke(): Result<Flow<List<PendingSubscription>>> = runCatching {
        pendingSubscriptionRepository.observePending()
    }
}
