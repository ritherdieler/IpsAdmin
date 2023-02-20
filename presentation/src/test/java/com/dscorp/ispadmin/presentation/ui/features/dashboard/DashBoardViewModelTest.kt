package com.dscorp.ispadmin.presentation.ui.features.dashboard

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.TestApp.KoinAppForInstrumentation
import com.dscorp.ispadmin.util.getValueForTest
import com.dscorp.ispadmin.util.registerIdlingResource
import com.dscorp.ispadmin.util.toJson
import com.example.cleanarchitecture.domain.domain.entity.DashBoardDataResponse
import com.example.data2.data.repository.IRepository
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = KoinAppForInstrumentation::class)
class DashBoardViewModelTest : AutoCloseKoinTest() {

    private val viewModel: DashBoardViewModel by inject()
    private lateinit var webServer: MockWebServer
    private lateinit var idlingResource: IdlingResource

    @Before
    fun setUp() {
        webServer = MockWebServer()
        idlingResource = registerIdlingResource()
        webServer.start(8081)

    }

    @After
    fun tearDown() {
        webServer.shutdown()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }


    @Test
    fun `when getDashBoardData is called, then should emit DashBoardData state`() {

        //given
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(DashBoardDataResponse().toJson())

        webServer.enqueue(response)

        //when
        viewModel.getDashBoardData()

        //then
        val value = viewModel.uiState.getValueForTest()
        assert(value is DashBoardDataUiState.DashBoardData)

    }

    @Test
    fun `when getDashBoardData is called, then should emit DashBoardDataError state`() {

        //given
        val response = MockResponse()
            .setResponseCode(400)
            .setBody(DashBoardDataResponse().toJson())

        webServer.enqueue(response)

        //when
        viewModel.getDashBoardData()

        //then
        val value = viewModel.uiState.getValueForTest()
        assert(value is DashBoardDataUiState.DashBoardDataError)

    }

}