package com.dscorp.ispadmin.observability

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ObservabilityApi {

    @POST("observability/events")
    suspend fun sendEvents(
        @Header("X-Obs-Api-Key") apiKey: String,
        @Header("X-Correlation-Id") correlationId: String,
        @Body body: ObsBatchRequest
    ): Response<ObsBatchResponse>

    @POST("observability/replays")
    suspend fun uploadReplay(
        @Header("X-Obs-Api-Key") apiKey: String,
        @Header("Content-Encoding") contentEncoding: String,
        @Query("format") format: String,
        @Query("sessionId") sessionId: String,
        @Query("durationMs") durationMs: Long,
        @Query("workflowId") workflowId: String? = null,
        @Body body: RequestBody
    ): Response<ObsReplayUploadResponse>
}
