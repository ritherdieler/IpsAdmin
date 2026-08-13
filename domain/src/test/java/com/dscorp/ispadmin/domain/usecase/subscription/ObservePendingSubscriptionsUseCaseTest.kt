package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ObservePendingSubscriptionsUseCaseTest {

    private lateinit var repository: PendingSubscriptionRepository
    private lateinit var useCase: ObservePendingSubscriptionsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObservePendingSubscriptionsUseCase(repository)
    }

    @Test
    fun `returns pending subscriptions flow from repository`() = runTest {
        val pending = listOf(
            PendingSubscription(
                localId = "local-1",
                clientRequestId = "client-1",
                subscriptionJson = """{"firstName":"Ana"}""",
                createdAt = 10L,
                status = PendingSubscriptionStatus.PENDING
            )
        )
        every { repository.observePending() } returns flowOf(pending)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(pending, result.getOrThrow().first())
        verify(exactly = 1) { repository.observePending() }
    }
}
