package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.FindSubscriptionUiState.*
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.jakewharton.espresso.OkHttp3IdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = KoinAppForInstrumentation::class)
class FindRegisterSubscriptionViewModelTest : AutoCloseKoinTest() {
    private val mockWebServer = MockWebServer()

    private lateinit var viewModel: FindSubscriptionViewModel

    private val id = 1003

    private val httpClient: OkHttpClient by inject()

    private lateinit var okHttp3IdlingResource: IdlingResource

    @Before
    fun setUp() {
        viewModel = FindSubscriptionViewModel()

        okHttp3IdlingResource = OkHttp3IdlingResource.create(
            "okhttp",
            httpClient
        )
        IdlingRegistry.getInstance().register(
            okHttp3IdlingResource
        )
        mockWebServer.start(8081)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        mockWebServer.shutdown()
    }

    @Test
    fun `when findSubscription is called then liveData should emit subscriptions`() = runTest {
        // given
        mockService(
            mockWebServer = mockWebServer,
            urlToMock = "/subscription/find?id=$id",
            response = MockResponse().fromJson("subscription/SubscriptionList.json")

        )

        // when
        viewModel.findSubscription(id)

        // then
        val value = viewModel.uiStateLiveData.getValueForTest() as OnSubscriptionFound
        assertNotNull(value)
    }

    @Test
    fun `when findSubscription has error then liveData should emit error`() = runTest {
        // given
        mockService(
            mockWebServer = mockWebServer,
            urlToMock = "/subscription/find?id=$id",
            response = MockResponse().setResponseCode(500)
        )

        // when
        viewModel.findSubscription(id)

        // then
        val value = viewModel.uiStateLiveData.getValueForTest() as OnError
        assertNotEquals("", value.message)
    }
}
