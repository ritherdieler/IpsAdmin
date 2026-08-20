package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.model.Subscription
import com.dscorp.ispadmin.domain.repository.SubscriptionActionsRepository

class RetryTr069ProvisioningUseCase(
    private val subscriptionActionsRepository: SubscriptionActionsRepository
) {
    suspend operator fun invoke(subscriptionId: Int): Result<Subscription> = runCatching {
        subscriptionActionsRepository.retryTr069Provisioning(subscriptionId)
    }
}
