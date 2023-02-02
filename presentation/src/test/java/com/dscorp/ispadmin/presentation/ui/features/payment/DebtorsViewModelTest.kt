package com.dscorp.ispadmin.presentation.ui.features.payment

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.presentation.ui.features.subscription.debtors.DebtorsUiState
import com.dscorp.ispadmin.presentation.ui.features.subscription.debtors.DebtorsViewModel
import com.dscorp.ispadmin.util.fromJson
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.mockService
import com.dscorp.ispadmin.util.registerIdlingResource
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
class DebtorsViewModelTest : KoinTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var viewModel: DebtorsViewModel
    private lateinit var okHttp3IdlingResource: IdlingResource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        viewModel = DebtorsViewModel()
        okHttp3IdlingResource = registerIdlingResource()
        mockWebServer.start(8081)

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        mockWebServer.shutdown()
        stopKoin()
    }

    @Test
    fun `when fetch debtors is called then return debtors list`() {
        //given
        mockGetDebtorsService()

        //when
        viewModel.fetchDebtors()
        Espresso.onIdle()

        //then
        val value = viewModel.uiState.getValueForTest()
        assert(value is DebtorsUiState.GetDebtorsSuccess)
    }

    @Test
    fun `when fetch debtors is called then return error`() {
        //given
        mockGetDebtorsServiceError()

        //when
        viewModel.fetchDebtors()
        Espresso.onIdle()

        //then
        val value = viewModel.uiState.getValueForTest()
        assert(value is DebtorsUiState.GetDebtorsError)
    }


    private fun mockGetDebtorsServiceError() {
        mockService(
            mockWebServer,
            "/subscription/debtors",
            MockResponse().setResponseCode(500)
        )
    }


    private fun mockGetDebtorsService() {
        mockService(
            mockWebServer,
            "/subscription/debtors",
            MockResponse().setResponseCode(200).fromJson("subscription/finddebtors/debtors.json")
        )
    }

}