package com.dscorp.ispadmin.data.connectivity

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MikrotikReachabilityMonitorImplTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `http response means reachable even if unauthorized`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401))
        val monitor = MikrotikReachabilityMonitorImpl(
            okHttpClient = OkHttpClient(),
            baseUrl = server.url("/ispadmin/").toString()
        )

        assertTrue(monitor.isMikrotikReachable())
    }

    @Test
    fun `timeout means unreachable`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        val monitor = MikrotikReachabilityMonitorImpl(
            okHttpClient = OkHttpClient(),
            baseUrl = server.url("/ispadmin/").toString(),
            timeoutMs = 200
        )

        assertFalse(monitor.isMikrotikReachable())
    }
}
