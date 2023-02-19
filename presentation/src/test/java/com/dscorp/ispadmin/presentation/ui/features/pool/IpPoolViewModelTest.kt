// package com.dscorp.ispadmin.presentation.ui.features.pool
//
// import androidx.test.espresso.IdlingRegistry
// import androidx.test.espresso.IdlingResource
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
// import com.dscorp.ispadmin.presentation.ui.features.ipPool.register.IpPoolUiState
// import com.dscorp.ispadmin.presentation.ui.features.ipPool.register.IpPoolViewModel
// import com.dscorp.ispadmin.util.*
// import com.example.cleanarchitecture.domain.domain.entity.IpPool
// import kotlinx.coroutines.test.runTest
// import okhttp3.mockwebserver.MockResponse
// import okhttp3.mockwebserver.MockWebServer
// import org.junit.After
// import org.junit.Assert.*
// import org.junit.Before
// import org.junit.Test
// import org.junit.runner.RunWith
// import org.koin.test.AutoCloseKoinTest
// import org.robolectric.annotation.Config
//
// @RunWith(AndroidJUnit4::class)
// @Config(application = KoinAppForInstrumentation::class)
// class IpPoolViewModelTest : AutoCloseKoinTest() {
//
//    private lateinit var viewModel: IpPoolViewModel
//    private val mockWebServer: MockWebServer = MockWebServer()
//    private lateinit var okHttp3IdlingResource: IdlingResource
//
//    @Before
//    fun setUp() {
//        viewModel = IpPoolViewModel()
//        okHttp3IdlingResource = registerIdlingResource()
//        mockWebServer.start(8081)
//    }
//
//    @After
//    fun tearDown() {
//        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
//        mockWebServer.shutdown()
//    }
//
//
//    @Test
//    fun `when get getIpPoolList is called then uiState equals to IpPoolList`() = runTest {
//        // Given
//        mockGetIpPoolListService()
//
//        // When
//        viewModel.getIpPoolList()
//
//        // Then
//        val value = viewModel.uiState.getValueForTest()
//        assert(value is IpPoolUiState.IpPoolList)
//    }
//
//    @Test
//    fun `when get getIpPoolList call has error then uiState equals to IpPoolListError`() = runTest {
//        // Given
//        mockGetIpPoolListServiceWithError()
//
//        // When
//        viewModel.getIpPoolList()
//
//        // Then
//        val value = viewModel.uiState.getValueForTest()
//        assertTrue(value is IpPoolUiState.IpPoolListError)
//    }
//
//    @Test
//    fun `when ip pool is created then return success`() = runTest {
//        // Given
//        mockRegisterIpPoolService()
//        val ipPool = IpPool(ipSegment = "192.168.100.0/24")
//
//        // When
//        viewModel.registerIpPool(ipPool)
//
//        // Then
//        val value = viewModel.uiState.getValueForTest()
//        assertTrue(value is IpPoolUiState.IpPoolRegister)
//    }
//
//    @Test
//    fun `when ip pool is created then return error`() = runTest {
//        // Given
//        mockRegisterIpPoolServiceWithError()
//        val ipPool = IpPool(ipSegment = "192.168.100.0/27")
//
//        // When
//        viewModel.registerIpPool(ipPool)
//
//        // Then
//        val value = viewModel.uiState.getValueForTest()
//        assertTrue(value is IpPoolUiState.IpPoolRegisterError)
//
//    }
//
//    @Test
//    fun `when ip segment is empty then return error`() = runTest {
//        // Given
//        val ipPool = IpPool(ipSegment = "")
//
//        // When
//        viewModel.registerIpPool(ipPool)
//
//        // Then
//        val value = viewModel.uiState.mGetValueForTest()
//        assertTrue(value is IpPoolUiState.IpPoolError)
//    }
//
//    @Test
//    fun `when ip segment is not valid then return error`() = runTest {
//        // Given
//        val ipPool = IpPool(ipSegment = "asdasdasdasd")
//
//        // When
//        viewModel.registerIpPool(ipPool)
//
//        // Then
//        val value = viewModel.uiState.mGetValueForTest()
//        assertTrue(value is IpPoolUiState.IpPoolInvalidIpSegment)
//    }
//
//    private fun mockGetIpPoolListServiceWithError() {
//        mockService(
//            mockWebServer = mockWebServer,
//            urlToMock = "/ip-pool",
//            response = MockResponse().setResponseCode(500)
//        )
//    }
//
//    private fun mockGetIpPoolListService() {
//        mockService(
//            mockWebServer = mockWebServer,
//            urlToMock = "/ip-pool",
//            response = MockResponse().setResponseCode(200).fromJson("ippool/list/success.json")
//        )
//    }
//
//    private fun mockRegisterIpPoolService() {
//        mockService(
//            mockWebServer = mockWebServer,
//            urlToMock = "/ip-pool",
//            response = MockResponse().setResponseCode(200).fromJson("ippool/register/success.json")
//        )
//    }
//
//    private fun mockRegisterIpPoolServiceWithError() {
//        mockService(
//            mockWebServer = mockWebServer,
//            urlToMock = "/ip-pool",
//            response = MockResponse().setResponseCode(500)
//        )
//    }
//
// }
//
