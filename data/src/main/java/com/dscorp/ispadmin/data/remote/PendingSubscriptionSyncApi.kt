package com.dscorp.ispadmin.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PendingSubscriptionSyncApi {
    @Multipart
    @POST("subscription/with-facade-photo")
    suspend fun registerWithFacadePhoto(
        @Part("subscription") subscription: RequestBody,
        @Part facadePhoto: MultipartBody.Part
    ): Response<Unit>
}
