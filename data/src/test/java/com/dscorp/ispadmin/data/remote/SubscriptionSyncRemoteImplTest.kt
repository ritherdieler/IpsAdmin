package com.dscorp.ispadmin.data.remote

import com.dscorp.ispadmin.domain.repository.SubscriptionSyncOutcome
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.RequestBody
import okio.Buffer
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.File

class SubscriptionSyncRemoteImplTest {

    @Test
    fun `uploadPending keeps clientIpAddress in subscription json body`() = runTest {
        val api = mockk<PendingSubscriptionSyncApi>()
        val bodySlot = slot<RequestBody>()
        coEvery { api.registerWithFacadePhoto(capture(bodySlot), any()) } returns Response.success(Unit)
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
}
