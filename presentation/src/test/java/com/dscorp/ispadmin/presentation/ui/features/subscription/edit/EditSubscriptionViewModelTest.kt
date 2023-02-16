package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState
import com.dscorp.ispadmin.util.*
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class EditSubscriptionViewModelTest : AutoCloseKoinTest() {

    private val mockServer: MockWebServer = MockWebServer()

    private lateinit var idlingResource: IdlingResource

    private val viewModel: SubscriptionViewModel by inject()

    @Before
    fun setUp() {
        mockServer.start(8081)
        idlingResource = registerIdlingResource()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
        unregisterIdlingResource(idlingResource)
    }

    @Test
    fun `when edit subscription is called then should return EditSubscriptionSuccess state`() {
        // Given
        val subscription = Subscription(
            id = 1,
            firstName = "sergio",
            lastName = "carrillo diestra",
            dni = "48271836",
            password = "111111111",
            address = "asdasd",
            phone = "999999999",
            subscriptionDate = 9989898989,
            isNew = false,
            serviceIsSuspended = false,
            planId = "123",
            networkDeviceIds = listOf(1, 2, 3),
            placeId = "1",
            location = GeoLocation(9999999.0,9999999.0),
            technicianId = 123,
            napBoxId = "123",
            hostDeviceId = 23
        )
        mockService(mockServer, "/subscription", MockResponse().setBody(subscription.toJson()))

        // When
        viewModel.editSubscription(subscription)
        val value = viewModel.editSubscriptionUiState.getValueForTest()

        // Then
        assertTrue(value is EditSubscriptionUiState.EditSubscriptionSuccess)

    }

    @Test
    fun `when subscription has invalid value in his fields then return error`()
    {
        // Given
        val subscription = Subscription(
            id = 1,
            firstName = "",
            lastName = "carrillo diestra",
            dni = "48271836",
            password = "111111111",
            address = "asdasd",
            phone = "999999999",
            subscriptionDate = 9989898989,
            isNew = false,
            serviceIsSuspended = false,
            planId = "123",
            networkDeviceIds = listOf(1, 2, 3),
            placeId = "1",
            location = GeoLocation(9999999.0,9999999.0),
            technicianId = 123,
            napBoxId = "123",
            hostDeviceId = 23
        )


        // When
        viewModel.editSubscription(subscription)
        val value = viewModel.editFormErrorLiveData.getValueForTest()

        // Then
        assertTrue(value is EditSubscriptionFormErrorUiState.OnEtFirstNameErrorRegisterUiState)
    }
}