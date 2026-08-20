package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.model.Subscription
import com.dscorp.ispadmin.domain.repository.SubscriptionActionsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RetryTr069ProvisioningUseCaseTest {

    private val subscriptionActionsRepository = mockk<SubscriptionActionsRepository>()
    private val useCase = RetryTr069ProvisioningUseCase(subscriptionActionsRepository)

    @Test
    fun `invoke returns success with updated subscription`() = runTest {
        val updated = Subscription(
            subscriptionId = 42,
            firstName = "Sergio",
            lastName = "Carrillo",
            tr069ProvisionStatus = "COMPLETE",
        )
        coEvery { subscriptionActionsRepository.retryTr069Provisioning(42) } returns updated

        val result = useCase(42)

        assertTrue(result.isSuccess)
        assertEquals("COMPLETE", result.getOrNull()?.tr069ProvisionStatus)
        coVerify(exactly = 1) { subscriptionActionsRepository.retryTr069Provisioning(42) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest {
        coEvery {
            subscriptionActionsRepository.retryTr069Provisioning(1)
        } throws Exception("OLT pendiente")

        val result = useCase(1)

        assertTrue(result.isFailure)
    }
}
