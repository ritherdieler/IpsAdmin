package com.dscorp.ispadmin.observability

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ObservabilitySpanApi {

    @POST("observability/spans")
    suspend fun sendSpans(
        @Header("X-Obs-Api-Key") apiKey: String,
        @Body spans: List<ObsSpanDto>
    ): Response<Unit>
}
