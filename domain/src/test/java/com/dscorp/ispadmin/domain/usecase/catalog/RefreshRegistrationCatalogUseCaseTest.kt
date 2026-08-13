package com.dscorp.ispadmin.domain.usecase.catalog

import com.dscorp.ispadmin.domain.repository.RegistrationCatalogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefreshRegistrationCatalogUseCaseTest {

    private lateinit var repository: RegistrationCatalogRepository
    private lateinit var useCase: RefreshRegistrationCatalogUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = RefreshRegistrationCatalogUseCase(repository)
    }

    @Test
    fun `invoke refreshes remote catalogs through repository`() = runTest {
        coEvery { repository.refreshFromRemote() } returns Result.success(Unit)

        val result = useCase()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.refreshFromRemote() }
    }

    @Test
    fun `invoke returns failure when remote refresh fails`() = runTest {
        val error = IllegalStateException("network down")
        coEvery { repository.refreshFromRemote() } returns Result.failure(error)

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("network down", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.refreshFromRemote() }
    }
}
