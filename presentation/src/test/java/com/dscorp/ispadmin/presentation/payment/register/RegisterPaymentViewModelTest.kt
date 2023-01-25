package com.dscorp.ispadmin.presentation.payment.register

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.KoinAppForInstrumentation
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = KoinAppForInstrumentation::class)
class RegisterPaymentViewModelTest : KoinTest {

    private val mockWebServer = MockWebServer()

    private lateinit var viewModel: RegisterPaymentViewModel

    private val httpClient: OkHttpClient by inject()

    private lateinit var okHttp3IdlingResource: IdlingResource

    @Before
    fun setUp() {
        viewModel = RegisterPaymentViewModel()
        okHttp3IdlingResource = registerIdlingResource(httpClient)
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }

    @Test
    fun `when register payment then return success`() {
        //Given
        val urlToMock = "/api/payment/register"
        val response = MockResponse().setResponseCode(200).fromJson("payment/register/success.json")
        mockService(mockWebServer = mockWebServer, urlToMock = urlToMock, response)

        //When
        //viewModel.registerPayment()

        //Then
        viewModel.registerPaymentUiState.observeForever {
            assertNotNull(it)
        }
    }


}