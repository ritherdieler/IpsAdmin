package com.dscorp.ispadmin.presentation.place

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dscorp.ispadmin.util.MainCoroutineRule
import com.dscorp.ispadmin.util.OkHttpProvider
import com.dscorp.ispadmin.util.fromJson
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.domain.entity.Place
import com.jakewharton.espresso.OkHttp3IdlingResource
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class PlaceViewModelTest : KoinTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    //
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource


    private val mockWebServer = MockWebServer()

    lateinit var placeViewModel: PlaceViewModel

    private val FAKE_BASE_URL = "http://127.0.0.1:8080"

    @Before
    fun setUp() {
//        startKoin {
//            allowOverride(true)
//
//            androidLogger()
//            // Reference Android context
////            androidContext(this@KoinApplication)
//            getKoin().run {
//                setProperty(BASE_URL, FAKE_BASE_URL)
//            }
//
//            loadKoinModules(
//                listOf(retrofitModule, viewModelModule, repositoryModule)
//            )
//        }
        okHttp3IdlingResource = OkHttp3IdlingResource.create(
            "okhttp",
            OkHttpProvider.getOkHttpClient()
        )
        IdlingRegistry.getInstance().register(okHttp3IdlingResource)

        mockWebServer.start(8080)
        placeViewModel = PlaceViewModel()
    }





    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource as IdlingResource)

        stopKoin()
    }

    @Test
    fun test() = runTest {

        mockApi()
        placeViewModel.registerPlace(
            Place(
                abbreviation = "StaNita", name = "Santa Anita", location = GeoLocation(0.0, 0.0)
            )
        )

        placeViewModel.placePlaceResponseLiveData.observeForever {
            assertEquals("StaNitaaa", (it as PlaceResponse.OnPlaceRegister).place.abbreviation)
        }
    }


    private fun mockApi() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/place" -> {
                        MockResponse().fromJson("PlaceRegistrationResponse.json")
                    }
                    else -> throw  Exception("No se encontro el path ${request.path}")
                }
            }
        }
    }

}