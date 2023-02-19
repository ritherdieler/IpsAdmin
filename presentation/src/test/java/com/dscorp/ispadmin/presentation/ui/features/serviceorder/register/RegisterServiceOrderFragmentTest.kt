package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.presentation.ui.features.serviceorder.register.RegisterServiceOrderFormError.*
import com.dscorp.ispadmin.presentation.ui.features.serviceorder.register.RegisterServiceOrderUiState.*
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class RegisterServiceOrderFragmentTest : AutoCloseKoinTest() {

    lateinit var mockWebServer: MockWebServer
    lateinit var viewModel: RegisterServiceOrderViewModel
    private lateinit var okHttp3IdlingResource: IdlingResource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        viewModel = RegisterServiceOrderViewModel()
        okHttp3IdlingResource = registerIdlingResource()
        mockWebServer.start(8081)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }

    @Test
    fun `when service order latitude or longitude is null then return error`() {
        // Given
        val serviceOrder = ServiceOrder(
            latitude = null,
            longitude = null,
            issue = "tiene un problema"
        )

        // When
        viewModel.registerServiceOrder(serviceOrder)

        // Then
        val value = viewModel.formErrorLiveData.getValueForTest() as OnEtLocationError
        assertEquals(value.error, RegisterServiceOrderFormError.LOCATION_ERROR)
    }

    @Test
    fun `when issue is empty then return error`() {
        // Given
        val serviceOrder = ServiceOrder(
            latitude = 20.0,
            longitude = 30.0,
            issue = "",
            subscriptionId = 1,
        )

        // When
        viewModel.registerServiceOrder(serviceOrder)

        // Then
        val value = viewModel.formErrorLiveData.getValueForTest() as OnEtIssueError
        assertEquals(value.error, RegisterServiceOrderFormError.ISSUE_ERROR)
    }

    @Test
    fun `when subscription id is empty then return error`() {
        // Given
        val serviceOrder = ServiceOrder(
            latitude = 20.0,
            longitude = 30.0,
            issue = "issue",
            subscriptionId = null,
        )

        // When
        viewModel.registerServiceOrder(serviceOrder)

        // Then
        val value = viewModel.formErrorLiveData.getValueForTest() as OnSubscriptionError
        assertEquals(value.error, RegisterServiceOrderFormError.SUBSCRIPTION_ERROR)
    }

    @Test
    fun `when service order is valid then return success`() {
        // Given
        mockRegisterServiceOrderService()
        val subscription = SubscriptionResponse(id = 1)
        val serviceOrder = ServiceOrder(
            latitude = 20.0,
            longitude = 30.0,
            issue = "issue",
            subscriptionId = 1
        )

        // When
        viewModel.subscription = subscription
        viewModel.registerServiceOrder(serviceOrder)

        // Then
        val value =
            viewModel.uiState.getValueForTest() as ServiceOrderRegisterSuccessOrder
        assert(value.serviceOrder.id != null)
    }

    private fun mockRegisterServiceOrderService() {
        mockService(
            mockWebServer,
            "/service-order",
            MockResponse().setResponseCode(200).fromJson("serviceorder/register/success.json")
        )
    }
}
