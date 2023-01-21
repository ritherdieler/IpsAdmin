package com.dscorp.ispadmin.presentation.subscriptionfinder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dscorp.ispadmin.KoinAppForInstrumentation
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.util.MainCoroutineScopeRule
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.example.data2.data.repository.IRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.inject
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.koin.test.inject
import org.mockito.Mockito
import org.mockito.Mockito.times
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = KoinAppForInstrumentation::class)
class FindSubscriptionViewModelTest : KoinTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    lateinit var mockWebServer: MockWebServer

    val repository: IRepository by inject()

    lateinit var viewModel: FindSubscriptionViewModel

    @Before
    fun setUp() {
        viewModel = FindSubscriptionViewModel()
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
//        mockWebServer.shutdown()
//        stopKoin()
    }

    @Test
    fun whenMainClicked_updatesTaps() = runTest {
        Mockito.`when`(repository.findSubscription(48271836)).thenReturn(subscriptionListMock)
        viewModel.findSubscription(48271836)
        Mockito.verify(repository, times(1)).findSubscription(48271836)
        val result = viewModel.uiStateLiveData.getValueForTest()
        assertEquals(
            subscriptionListMock,
            (result as FindSubscriptionUiState.OnSubscriptionFound).subscriptions
        )
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