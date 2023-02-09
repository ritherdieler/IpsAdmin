package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryViewModel
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class PaymentHistoryViewModelTest : KoinTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var viewModel: PaymentHistoryViewModel

    private lateinit var okHttp3IdlingResource: IdlingResource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        viewModel = PaymentHistoryViewModel()
        okHttp3IdlingResource = registerIdlingResource()
        mockWebServer.start(8081)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        stopKoin()
    }

    @Test
    fun `when payment history is called then return a list of payments`() {
        // Given
        mockGetPaymentHistoryService()
        val request = SearchPaymentsRequest(
            subscriptionId = 1, startDate = 1, endDate = 2
        )
        // When
        viewModel.getPaymentHistory(request)
        Espresso.onIdle()

        // Then
        val value = viewModel.resultLiveData.getValueForTest()
        assertTrue(value is PaymentHistoryUiState.OnPaymentResponseHistory)
    }

    @Test
    fun `when throw error then emmit OnError`() {
        // Given
        mockErrorGetPaymentHistoryService()
        val request = SearchPaymentsRequest(subscriptionId = 1, startDate = 1, endDate = 2)

        // When
        viewModel.getPaymentHistory(request)
        Espresso.onIdle()

        // Then
        val value = (viewModel.resultLiveData.getValueForTest() as PaymentHistoryUiState.OnError)
        assertTrue(!value.message.isNullOrEmpty())
    }

    private fun mockGetPaymentHistoryService() {
        val urlToMock = "/payment?subscriptionId=1&startDate=1&endDate=2"
        val response =
            MockResponse().setResponseCode(200).fromJson("payment/history/payment_list.json")
        mockService(mockWebServer = mockWebServer, urlToMock = urlToMock, response)
    }

    private fun mockErrorGetPaymentHistoryService() {
        val urlToMock = "/payment?subscriptionId=1&startDate=1&endDate=2"
        val response =
            MockResponse().setResponseCode(500).fromJson("payment/history/payment_list.json")
        mockService(mockWebServer = mockWebServer, urlToMock = urlToMock, response)
    }
}