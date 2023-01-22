package com.dscorp.ispadmin.presentation.subscriptionfinder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.KoinAppForInstrumentation
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.util.asAndroidX
import com.dscorp.ispadmin.util.fromJson
import com.jakewharton.espresso.OkHttp3IdlingResource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
//@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = KoinAppForInstrumentation::class)
class FindSubscriptionViewModelTest : KoinTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

//    @get:Rule
//    val coroutineScope = MainCoroutineScopeRule()

    val mockWebServer = MockWebServer()

    lateinit var viewModel: FindSubscriptionViewModel

    private val DNI = 48271836

    private val httpClient: OkHttpClient by inject()

    var resource = OkHttp3IdlingResource.create("OkHttp", httpClient).asAndroidX()

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(resource)
        viewModel = FindSubscriptionViewModel()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        stopKoin()
    }

    @Test
    fun whenMainClicked_updatesTaps() = runTest {
        launch {
            mockApi()
            viewModel.findSubscription(DNI)
            Espresso.onIdle()
            viewModel.uiStateLiveData.observeForever {
                assertNotNull(
                    (it as FindSubscriptionUiState.OnSubscriptionFound).subscriptions
                )
            }
        }
    }

    private fun mockApi() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/subscription/find" -> {
                        MockResponse().fromJson("SubscriptionList.json")
                    }
                    else -> throw  Exception("No se encontro el path ${request.path}")
                }
            }
        }
    }

}