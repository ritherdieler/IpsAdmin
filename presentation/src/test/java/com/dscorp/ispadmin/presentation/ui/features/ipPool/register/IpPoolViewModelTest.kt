package com.dscorp.ispadmin.presentation.ui.features.ipPool.register

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
import com.example.cleanarchitecture.domain.domain.entity.IpPool
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class IpPoolViewModelTest : KoinTest {

    private lateinit var viewModel: IpPoolViewModel

    private lateinit var mockWebServer: MockWebServer

    private lateinit var okHttp3IdlingResource: IdlingResource

    @Before
    fun setUp() {
        viewModel = IpPoolViewModel()
        mockWebServer = MockWebServer()
        mockWebServer.start(8081)
        okHttp3IdlingResource = registerIdlingResource()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        stopKoin()
    }


    @Test
    fun `when get getIpPoolList is called then uiState equals to IpPoolList`() {
        // Given
        mockGetIpPoolListService()

        // When
        viewModel.getIpPoolList()
        Espresso.onIdle()

        // Then
        val value = viewModel.uiState.getValueForTest()
        assert(value is IpPoolUiState.IpPoolList)
    }

    @Test
    fun `when get getIpPoolList call has error then uiState equals to IpPoolListError`() {
        // Given
        mockGetIpPoolListServiceWithError()

        // When
        viewModel.getIpPoolList()
        Espresso.onIdle()

        // Then
        val value = viewModel.uiState.getValueForTest()
        assert(value is IpPoolUiState.IpPoolListError)
    }

    private fun mockGetIpPoolListServiceWithError() {
        mockService(
            mockWebServer = mockWebServer,
            urlToMock = "/ip-pool",
            response = MockResponse().setResponseCode(500)
        )
    }

    private fun mockGetIpPoolListService() {
        mockService(
            mockWebServer = mockWebServer,
            urlToMock = "/ip-pool",
            response = MockResponse().setResponseCode(200).fromJson("ippool/list/success.json")
        )
    }

    @Test
    fun `when ip pool is created then return success`() {
        // Given
        mockRegisterIpPoolService()
        val ipPool = IpPool(ipSegment = "192.168.100.0/24")

        // When
        viewModel.registerIpPool(ipPool)
        Espresso.onIdle()

        // Then
        val value = viewModel.uiState.getValueForTest()
        assert(value is IpPoolUiState.IpPoolRegister)
    }

    @Test
    fun `when ip pool is created then return error`() {
        // Given
        mockRegisterIpPoolServiceWithError()
        val ipPool = IpPool(ipSegment = "192.168.100.0/27")

        // When
        viewModel.registerIpPool(ipPool)
        Espresso.onIdle()

        // Then
        val value = viewModel.uiState.getValueForTest()
        assert(value is IpPoolUiState.IpPoolRegisterError)

    }

    @Test
    fun `when ip segment is empty then return error`() {
        // Given
        val ipPool = IpPool(ipSegment = "")

        // When
        viewModel.registerIpPool(ipPool)

        // Then
        val value = viewModel.uiState.getValueForTest()
        assert(value is IpPoolUiState.IpPoolError)
    }

    @Test
    fun `when ip segment is not valid then return error`() {
        // Given
        val ipPool = IpPool(ipSegment = "asdasdasdasd")

        // When
        viewModel.registerIpPool(ipPool)
        Espresso.onIdle()

        // Then
        val value = viewModel.uiState.getValueForTest()
        assert(value is IpPoolUiState.IpPoolInvalidIpSegment)
    }


    private fun mockRegisterIpPoolService() {
        mockService(
            mockWebServer = mockWebServer,
            urlToMock = "/ip-pool",
            response = MockResponse().setResponseCode(200).fromJson("ippool/register/success.json")
        )
    }

    private fun mockRegisterIpPoolServiceWithError() {
        mockService(
            mockWebServer = mockWebServer,
            urlToMock = "/ip-pool",
            response = MockResponse().setResponseCode(500)
        )
    }

}

