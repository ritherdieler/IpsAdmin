package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import com.dscorp.ispadmin.domain.storage.FacadePhotoStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

class EnqueuePendingSubscriptionUseCaseTest {

    private lateinit var pendingRepository: PendingSubscriptionRepository
    private lateinit var photoStorage: FacadePhotoStorage
    private lateinit var useCase: EnqueuePendingSubscriptionUseCase
    private lateinit var photoFile: File

    @Before
    fun setUp() {
        pendingRepository = mockk()
        photoStorage = mockk()
        photoFile = File.createTempFile("facade", ".jpg").apply { writeText("photo") }
        coEvery { pendingRepository.enqueue(any()) } returns Result.success(Unit)
        every { photoStorage.save(any(), any()) } answers {
            val localId = firstArg<String>()
            File("/files/pending_subscriptions/$localId.jpg")
        }
        useCase = EnqueuePendingSubscriptionUseCase(pendingRepository, photoStorage)
    }

    @Test
    fun `generates unique uuid clientRequestId`() = runTest {
        val first = useCase(
            subscriptionJson = """{"dni":"111"}""",
            facadePhotoFile = photoFile,
            installationOrderId = 9
        ).getOrThrow()
        val second = useCase(
            subscriptionJson = """{"dni":"222"}""",
            facadePhotoFile = photoFile,
            installationOrderId = 9
        ).getOrThrow()

        UUID.fromString(first.clientRequestId)
        UUID.fromString(second.clientRequestId)
        assertNotEquals(first.clientRequestId, second.clientRequestId)
        assertNotEquals(first.localId, second.localId)
    }

    @Test
    fun `saves facade photo under pending_subscriptions localId jpg`() = runTest {
        val result = useCase(
            subscriptionJson = """{"dni":"111"}""",
            facadePhotoFile = photoFile,
            installationOrderId = null
        ).getOrThrow()

        io.mockk.verify { photoStorage.save(result.localId, photoFile) }
        val normalizedPath = result.facadePhotoPath!!.replace("\\", "/")
        assertTrue(normalizedPath.endsWith("pending_subscriptions/${result.localId}.jpg"))
    }

    @Test
    fun `inserts pending subscription json in room with PENDING status`() = runTest {
        val captured = slot<PendingSubscription>()
        coEvery { pendingRepository.enqueue(capture(captured)) } returns Result.success(Unit)

        val result = useCase(
            subscriptionJson = """{"firstName":"Ana","clientIpAddress":"192.168.1.77"}""",
            facadePhotoFile = photoFile,
            installationOrderId = 42
        ).getOrThrow()

        assertEquals(PendingSubscriptionStatus.PENDING, captured.captured.status)
        assertEquals(
            """{"firstName":"Ana","clientIpAddress":"192.168.1.77"}""",
            captured.captured.subscriptionJson
        )
        assertEquals(42, captured.captured.installationOrderId)
        assertEquals(result.clientRequestId, captured.captured.clientRequestId)
        coVerify(exactly = 1) { pendingRepository.enqueue(any()) }
    }
}
