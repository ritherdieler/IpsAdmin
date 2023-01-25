package com.dscorp.ispadmin.util

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.koin.test.KoinTest

fun KoinTest.mockService(mockWebServer: MockWebServer, urlToMock: String, response: MockResponse) {
    mockWebServer.dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                urlToMock -> response
                else -> throw Exception("No se encontro el path ${request.path}")
            }
        }
    }
}

fun KoinTest.registerIdlingResource(httpClient: OkHttpClient): IdlingResource {
    val okHttp3IdlingResource = OkHttp3IdlingResource.create(
        "okhttp",
        httpClient
    ).asAndroidX()
    IdlingRegistry.getInstance().register(
        okHttp3IdlingResource
    )
    return okHttp3IdlingResource
}



