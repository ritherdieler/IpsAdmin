package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.connectivity.MikrotikReachabilityMonitor
import com.dscorp.ispadmin.domain.connectivity.NetworkConnectivityMonitor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveOfflineRegistrationModeUseCaseTest {

    private val connectivity = mockk<NetworkConnectivityMonitor>()
    private val reachability = mockk<MikrotikReachabilityMonitor>()
    private val useCase = ObserveOfflineRegistrationModeUseCase(connectivity, reachability)

    @Test
    fun `offline when device has no internet`() = runTest {
        every { connectivity.observeConnectivity() } returns flowOf(false)

        val offline = useCase().getOrThrow().first()

        assertTrue(offline)
    }

    @Test
    fun `offline when internet is up but MikroTik API is unreachable`() = runTest {
        every { connectivity.observeConnectivity() } returns flowOf(true)
        coEvery { reachability.isMikrotikReachable() } returns false

        val offline = useCase().getOrThrow().first()

        assertTrue(offline)
    }

    @Test
    fun `online when internet and MikroTik API are reachable`() = runTest {
        every { connectivity.observeConnectivity() } returns flowOf(true)
        coEvery { reachability.isMikrotikReachable() } returns true

        val offline = useCase().getOrThrow().first()

        assertFalse(offline)
    }
}
