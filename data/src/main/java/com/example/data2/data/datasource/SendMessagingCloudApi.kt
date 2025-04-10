package com.example.data2.data.datasource

import com.example.cleanarchitecture.domain.entity.FirebaseBody
import com.example.cleanarchitecture.domain.entity.FireBaseResponse
import com.example.data2.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SendMessagingCloudApi {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=${BuildConfig.SEND_CLOUD_MESSAGING_API_KEY}"
    )
    @POST("fcm/send")
   suspend fun sendCloudMessaging(@Body body: FirebaseBody?):Response<FireBaseResponse>
}