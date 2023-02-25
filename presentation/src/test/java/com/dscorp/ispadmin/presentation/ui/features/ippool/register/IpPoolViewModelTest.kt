package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
import com.dscorp.ispadmin.util.toJson
import com.example.cleanarchitecture.domain.domain.entity.IpPool
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class IpPoolViewModelTest : AutoCloseKoinTest() {

    private lateinit var webServer: MockWebServer
    private lateinit var idlingResource: IdlingResource
    private val viewModel: IpPoolViewModel by inject()

    @Before
    fun setUp() {
        idlingResource = registerIdlingResource()
        webServer = MockWebServer()
        webServer.start(8081)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        webServer.shutdown()
    }


    @Test
    fun `when Ip pool hostDeviceId is null then should return error state`(){
        // Given
        val ipPool = IpPool(ipSegment = "192.168.10.1/24", hostDeviceId = null)

        // When
        viewModel.registerIpPool(ipPool)

        // Then
        val state = viewModel.uiState.getValueForTest()
        assertTrue(state is IpPoolUiState.NoHostDeviceSelectedError)
    }

    @Test
    fun `when get host devices has errors then should return error state`(){
        // Given
        mockErrorGetHostDevicesService()

        // When
        viewModel.getHostDevices()

        // Then
        val state = viewModel.uiState.getValueForTest()
        assertTrue(state is IpPoolUiState.HostDevicesError)
    }

    private fun mockErrorGetHostDevicesService() {
        val response = MockResponse().setResponseCode(500)
        mockService(webServer, "/networkDevice/coreTypes", response)
    }

    @Test
    fun `when get host devices then return should hostDeviceSuccess state`() {
        // Given
        mockGetHostDevicesService()

        // When
        viewModel.getHostDevices()

        // Then
        val state = viewModel.uiState.getValueForTest()

        assertTrue(state is IpPoolUiState.HostDevicesReady)
    }

    private fun mockGetHostDevicesService() {

        val hostDevices = listOf(
            NetworkDevice(
                id = 1,
                name = "asdad",
                password = "123",
                username = "asd",
                ipAddress = "192.168.100.1",
                networkDeviceType = "1"
            ),
            NetworkDevice(
                id = 2,
                name = "asdad",
                password = "123",
                username = "asd",
                ipAddress = "192.168.100.2",
                networkDeviceType = "1"
            )
        )

        val response = MockResponse().setResponseCode(200).setBody(hostDevices.toJson())
        mockService(webServer, "/networkDevice/coreTypes", response)

    }


}