package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.CustomerData
import com.dscorp.ispadmin.domain.model.GeoLocation
import com.dscorp.ispadmin.domain.model.InstallationType
import com.dscorp.ispadmin.domain.model.PagedResult
import com.dscorp.ispadmin.domain.model.ServiceStatus
import com.dscorp.ispadmin.domain.model.SubscriptionResume
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchSubscriptionsUseCaseTest {

    private val repository = mockk<IRepository>()
    private val useCase = SearchSubscriptionsUseCase(repository)

    private fun resume(id: Int) = SubscriptionResume(
        id = id,
        planName = "Plan",
        customerName = "JUAN PEREZ",
        antiquity = "1",
        qualification = "A",
        placeName = "Lima",
        ics = "1",
        lastPaymentDate = null,
        pendingInvoicesQuantity = 0,
        totalDebt = 0.0,
        ipAddress = "",
        customer = CustomerData(
            subscriptionId = id,
            name = "JUAN",
            lastName = "PEREZ",
            dni = "12345678",
            place = "Lima",
            address = "Calle 1",
            phone = "999999999",
            email = ""
        ),
        serviceStatus = ServiceStatus.ACTIVE,
        installationType = InstallationType.FIBER,
        napBox = null,
        placeId = "1",
        location = GeoLocation()
    )

    @Test
    fun `invoke returns paged result on success`() = runTest {
        val paged = PagedResult(items = listOf(resume(1), resume(2)), page = 0, size = 20, total = 2, totalPages = 1)
        coEvery { repository.searchSubscriptions("juan", null, 0, 20) } returns paged

        val result = useCase("juan", null, 0, 20)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.items?.size)
        coVerify(exactly = 1) { repository.searchSubscriptions("juan", null, 0, 20) }
    }

    @Test
    fun `invoke trims query before delegating to repository`() = runTest {
        coEvery { repository.searchSubscriptions("juan perez", null, 0, 20) } returns PagedResult()

        useCase("  juan perez  ", null, 0, 20)

        coVerify(exactly = 1) { repository.searchSubscriptions("juan perez", null, 0, 20) }
    }

    @Test
    fun `invoke returns empty paged result when there are no matches`() = runTest {
        coEvery { repository.searchSubscriptions(any(), any(), any(), any()) } returns PagedResult()

        val result = useCase("zzz", null, 0, 20)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.items.isNullOrEmpty())
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest {
        coEvery { repository.searchSubscriptions(any(), any(), any(), any()) } throws Exception("network")

        val result = useCase("juan", null, 0, 20)

        assertTrue(result.isFailure)
    }
}
