package com.dscorp.ispadmin.data.remote

import com.dscorp.ispadmin.domain.repository.SubscriptionSyncOutcome
import com.dscorp.ispadmin.domain.repository.SubscriptionSyncRemote
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class SubscriptionSyncRemoteImpl(
    private val api: PendingSubscriptionSyncApi
) : SubscriptionSyncRemote {

    override suspend fun uploadPending(
        subscriptionJson: String,
        clientRequestId: String,
        installationOrderId: Int?,
        facadePhotoFile: File?
    ): SubscriptionSyncOutcome {
        return try {
            val payload = mergeIdentity(subscriptionJson, clientRequestId, installationOrderId)
            val subscriptionBody = payload.toRequestBody("application/json".toMediaTypeOrNull())
            val photoFile = facadePhotoFile ?: return SubscriptionSyncOutcome.Failure("Falta foto de fachada")
            val photoPart = MultipartBody.Part.createFormData(
                name = "facadePhoto",
                filename = photoFile.name,
                body = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val response = api.registerWithFacadePhoto(subscriptionBody, photoPart)
            when (response.code()) {
                200, 201 -> SubscriptionSyncOutcome.Success
                409 -> SubscriptionSyncOutcome.Conflict
                else -> SubscriptionSyncOutcome.Failure("HTTP ${response.code()}")
            }
        } catch (error: IOException) {
            SubscriptionSyncOutcome.Failure(error.message ?: "Error de red")
        }
    }

    private fun mergeIdentity(
        subscriptionJson: String,
        clientRequestId: String,
        installationOrderId: Int?
    ): String {
        val json = JsonParser.parseString(subscriptionJson).asJsonObject
        json.addProperty("clientRequestId", clientRequestId)
        if (installationOrderId != null) {
            json.addProperty("installationOrderId", installationOrderId)
        }
        return json.toString()
    }
}
