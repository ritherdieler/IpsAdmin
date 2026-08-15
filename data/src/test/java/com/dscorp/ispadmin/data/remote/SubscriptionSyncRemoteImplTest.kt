package com.dscorp.ispadmin.data.remote

import com.dscorp.ispadmin.domain.repository.SubscriptionSyncOutcome
import com.dscorp.ispadmin.domain.repository.SubscriptionSyncRemote
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.File

class SubscriptionSyncRemoteImplTest {

    @Test
    fun `uploadPending keeps clientIpAddress in subscription json body`() = runTest {
        val api = mockk<PendingSubscriptionSyncApi>()
        val bodySlot = slot<RequestBody>()
        coEvery { api.registerWithFacadePhoto(capture(bodySlot), any()) } returns
            Response.success(SubscriptionSyncApiResponse(status = 200))
        val remote = SubscriptionSyncRemoteImpl(api)
        val photo = File.createTempFile("facade", ".jpg").apply { writeText("photo") }

        val outcome = remote.uploadPending(
            subscriptionJson = """{"firstName":"Ana","clientIpAddress":"192.168.1.77"}""",
            clientRequestId = "req-1",
            installationOrderId = 8,
            facadePhotoFile = photo
        )

        assertTrue(outcome is SubscriptionSyncOutcome.Success)
        val buffer = Buffer()
        bodySlot.captured.writeTo(buffer)
        val json = buffer.readUtf8()
        assertTrue(json.contains("clientIpAddress"))
        assertTrue(json.contains("192.168.1.77"))
        assertTrue(json.contains("req-1"))
    }

    @Test
    fun `maps body status 409 with IP_CONFLICT to IpConflict`() = runTest {
        val api = mockk<PendingSubscriptionSyncApi>()
        coEvery { api.registerWithFacadePhoto(any(), any()) } returns Response.success(
            SubscriptionSyncApiResponse(
                status = 409,
                error = "La IP del cliente ya esta en uso",
                errorCode = "IP_CONFLICT"
            )
        )
        val remote: SubscriptionSyncRemote = SubscriptionSyncRemoteImpl(api)
        val photo = File.createTempFile("facade", ".jpg").apply { writeText("photo") }

        val outcome = remote.uploadPending(
            subscriptionJson = """{"clientIpAddress":"192.168.1.77"}""",
            clientRequestId = "req-ip",
            installationOrderId = null,
            facadePhotoFile = photo
        )

        assertEquals(SubscriptionSyncOutcome.IpConflict, outcome)
    }

    @Test
    fun `maps body status 409 without errorCode to Conflict`() = runTest {
        val api = mockk<PendingSubscriptionSyncApi>()
        coEvery { api.registerWithFacadePhoto(any(), any()) } returns Response.success(
            SubscriptionSyncApiResponse(
                status = 409,
                error = "Este usuario ya se encuentra registrado"
            )
        )
        val remote = SubscriptionSyncRemoteImpl(api)
        val photo = File.createTempFile("facade", ".jpg").apply { writeText("photo") }

        val outcome = remote.uploadPending(
            subscriptionJson = """{"dni":"123"}""",
            clientRequestId = "req-2",
            installationOrderId = null,
            facadePhotoFile = photo
        )

        assertEquals(SubscriptionSyncOutcome.Conflict, outcome)
    }

    @Test
    fun `maps http 409 error body with IP_CONFLICT to IpConflict`() = runTest {
        val api = mockk<PendingSubscriptionSyncApi>()
        val errorJson =
            """{"status":409,"error":"IP en uso","errorCode":"IP_CONFLICT"}"""
        coEvery { api.registerWithFacadePhoto(any(), any()) } returns Response.error(
            409,
            errorJson.toResponseBody("application/json".toMediaType())
        )
        val remote = SubscriptionSyncRemoteImpl(api)
        val photo = File.createTempFile("facade", ".jpg").apply { writeText("photo") }

        val outcome = remote.uploadPending(
            subscriptionJson = """{"clientIpAddress":"10.0.0.2"}""",
            clientRequestId = "req-3",
            installationOrderId = null,
            facadePhotoFile = photo
        )

        assertEquals(SubscriptionSyncOutcome.IpConflict, outcome)
    }
}
