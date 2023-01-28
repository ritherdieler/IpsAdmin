package com.dscorp.ispadmin.presentation.payment.register

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.KoinAppForInstrumentation
import com.dscorp.ispadmin.presentation.payment.register.RegisterPaymentErrorUiState.*
import com.dscorp.ispadmin.presentation.payment.register.RegisterPaymentUiState.*
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.cleanarchitecture.domain.domain.entity.Plan
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
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
    fun `when register payment then return success`() {
        //Given
        mockRegisterPaymentService()

        val plan = Plan(id = null, name = "", price = 100.0, downloadSpeed = "", uploadSpeed = "")
        val payment = Payment(
            id = 1,
            amountPaid = 110.0,
            date = -1,
            subscriptionId = 1,
            method = "Yape",
            discount = 10.0,
            paid = false
        )
        viewModel.subscription = SubscriptionResponse().apply { this.plan = plan }

        //When

        viewModel.registerPayment(payment)

        //Then
        Espresso.onIdle()
        val value = viewModel.registerPaymentState.value as OnPaymentRegistered
        assertNotNull(value.payment.id != null)
    }

    @Test
    fun ` when payment amount is less than 0 then return error`() {
        //Given
        val payment = Payment(
            id = 1,
            amountPaid = -1.0,
            date = 0,
            subscriptionId = 1,
            method = "Yape",
            discount = 1.0,
            paid = false
        )

        //when
        viewModel.registerPayment(payment)

        //then
        viewModel.registerPaymentFormErrorState.getValueForTest()
        val value = viewModel.registerPaymentFormErrorState.value as InvalidAmountError
        assertEquals(value.message, RegisterPaymentErrorUiState.ERROR_INVALID_AMOUNT)

    }

    @Test
    fun ` when discount is less than 0 then return error`() {
        //Given
        val payment = Payment(
            id = 1,
            amountPaid = 10.0,
            date = 0,
            subscriptionId = 1,
            method = "Yape",
            discount = -1.0,
            paid = false
        )

        //when
        viewModel.registerPayment(payment)

        //then
        viewModel.registerPaymentFormErrorState.getValueForTest()
        val value = viewModel.registerPaymentFormErrorState.value as InvalidDiscountError
        assertEquals(value.message, RegisterPaymentErrorUiState.ERROR_INVALID_DISCOUNT)

    }

    @Test
    fun ` when payment method is empty then return error`() {
        //Given
        val payment = Payment(
            id = 1,
            amountPaid = 10.0,
            date = 0,
            subscriptionId = 1,
            method = "",
            discount = 1.0,
            paid = false
        )

        //when
        viewModel.registerPayment(payment)

        //then
        viewModel.registerPaymentFormErrorState.getValueForTest()
        val value = viewModel.registerPaymentFormErrorState.value as InvalidMethodError
        assertEquals(value.message, RegisterPaymentErrorUiState.ERROR_INVALID_METHOD)
    }

    @Test
    fun ` when payment subscription id is less or equal to 0 then return error`() {
        //Given
        val payment = Payment(
            id = 0,
            amountPaid = 10.0,
            date = 0,
            subscriptionId = -1,
            method = "Yape",
            discount = 1.0,
            paid = false
        )

        //when
        viewModel.registerPayment(payment)

        //then
        viewModel.registerPaymentFormErrorState.getValueForTest()
        val value = viewModel.registerPaymentFormErrorState.value as GenericError
        assertEquals(value.message, RegisterPaymentErrorUiState.ERROR_GENERIC)
    }

    @Test
    fun `when discount is greater than plan price then return error`() {
        //Given
        val plan = Plan(id = null, name = "", price = 50.0, downloadSpeed = "", uploadSpeed = "")
        val payment = Payment(
            id = 1,
            amountPaid = 50.0,
            date = -1,
            subscriptionId = 1,
            method = "Yape",
            discount = 55.0,
            paid = false
        )
        viewModel.subscription = SubscriptionResponse().apply { this.plan = plan }

        //When
        viewModel.registerPayment(payment)

        //Then
        viewModel.registerPaymentFormErrorState.getValueForTest()
        val value = viewModel.registerPaymentFormErrorState.value as InvalidDiscountError
        assertEquals(value.message, RegisterPaymentErrorUiState.ERROR_INVALID_DISCOUNT)
    }


    private fun mockRegisterPaymentService() {
        val urlToMock = "/payment"
        val response = MockResponse().setResponseCode(200).fromJson("payment/register/success.json")
        mockService(mockWebServer = mockWebServer, urlToMock = urlToMock, response)
    }
}