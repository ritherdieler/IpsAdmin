package com.dscorp.ispadmin.domain.usecase.catalog

import com.dscorp.ispadmin.domain.exception.CatalogNotAvailableOffline
import com.dscorp.ispadmin.domain.model.CatalogCoreDevice
import com.dscorp.ispadmin.domain.model.CatalogNapBox
import com.dscorp.ispadmin.domain.model.CatalogOnu
import com.dscorp.ispadmin.domain.model.CatalogPlace
import com.dscorp.ispadmin.domain.model.CatalogPlan
import com.dscorp.ispadmin.domain.model.RegistrationCatalog
import com.dscorp.ispadmin.domain.repository.RegistrationCatalogRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class GetRegistrationCatalogUseCaseTest {

    private lateinit var repository: RegistrationCatalogRepository
    private lateinit var useCase: GetRegistrationCatalogUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetRegistrationCatalogUseCase(repository)
    }

    @Test
    fun `returns success with coreDevices when cache is populated`() = runTest {
        val catalog = populatedCatalog()
        coEvery { repository.getCachedCatalog() } returns Result.success(catalog)

        val result = useCase()

        assertTrue(result.isSuccess)
        val value = result.getOrThrow()
        assertTrue(value.coreDevices.isNotEmpty())
        assertEquals("Core-1", value.coreDevices.first().name)
        assertEquals(catalog.plans, value.plans)
    }

    @Test
    fun `returns CatalogNotAvailableOffline when cache is empty and there is no network fallback`() = runTest {
        coEvery { repository.getCachedCatalog() } returns Result.failure(CatalogNotAvailableOffline())

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CatalogNotAvailableOffline)
    }

    @Test
    fun `returns CatalogNotAvailableOffline when cached catalog has no coreDevices`() = runTest {
        coEvery { repository.getCachedCatalog() } returns Result.success(
            populatedCatalog().copy(coreDevices = emptyList())
        )

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CatalogNotAvailableOffline)
    }

    private fun populatedCatalog() = RegistrationCatalog(
        plans = listOf(CatalogPlan(id = "plan-1", name = "100 Mbps")),
        places = listOf(CatalogPlace(id = "place-1", name = "Huacho")),
        napBoxes = listOf(CatalogNapBox(id = "nap-1", code = "NAP-01")),
        onus = listOf(CatalogOnu(sn = "ONU123")),
        coreDevices = listOf(CatalogCoreDevice(id = 1, name = "Core-1"))
    )
}
