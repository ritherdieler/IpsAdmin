package com.dscorp.ispadmin.util

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
                else -> throw  Exception("No se encontro el path ${request.path}")
            }
        }
    }
}