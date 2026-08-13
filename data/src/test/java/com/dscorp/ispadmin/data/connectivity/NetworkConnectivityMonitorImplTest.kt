package com.dscorp.ispadmin.data.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.dscorp.ispadmin.domain.connectivity.NetworkConnectivityMonitor
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkConnectivityMonitorImplTest {

    @Test
    fun `isConnected returns false when there is no active network`() {
        val connectivityManager = mockk<ConnectivityManager>()
        every { connectivityManager.activeNetwork } returns null
        val monitor: NetworkConnectivityMonitor = NetworkConnectivityMonitorImpl(connectivityManager)

        assertFalse(monitor.isConnected())
    }

    @Test
    fun `isConnected returns true when internet capability is present`() {
        val connectivityManager = mockk<ConnectivityManager>()
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        val monitor = NetworkConnectivityMonitorImpl(connectivityManager)

        assertTrue(monitor.isConnected())
    }
}
