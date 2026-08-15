package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.domain.connectivity.NetworkConnectivityMonitor
import com.dscorp.ispadmin.domain.exception.NoConnectivity
import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import com.dscorp.ispadmin.domain.repository.SubscriptionSyncOutcome
import com.dscorp.ispadmin.domain.repository.SubscriptionSyncRemote
import com.dscorp.ispadmin.domain.storage.FacadePhotoStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class SyncPendingSubscriptionsUseCaseTest {

    private lateinit var connectivity: NetworkConnectivityMonitor
    private lateinit var pendingRepository: PendingSubscriptionRepository
    private lateinit var photoStorage: FacadePhotoStorage
    private lateinit var remote: SubscriptionSyncRemote
    private lateinit var useCase: SyncPendingSubscriptionsUseCase

    @Before
    fun setUp() {
        connectivity = mockk()
        pendingRepository = mockk(relaxed = true)
        photoStorage = mockk(relaxed = true)
        remote = mockk()
        every { connectivity.isConnected() } returns true
        coEvery { pendingRepository.markSyncing(any()) } returns Result.success(Unit)
        coEvery { pendingRepository.deleteByLocalId(any()) } returns Result.success(Unit)
        coEvery { pendingRepository.markConflict(any(), any()) } returns Result.success(Unit)
        coEvery { pendingRepository.markRetryableError(any(), any()) } returns Result.success(Unit)
        useCase = SyncPendingSubscriptionsUseCase(
            connectivityMonitor = connectivity,
            pendingSubscriptionRepository = pendingRepository,
            facadePhotoStorage = photoStorage,
            subscriptionSyncRemote = remote
        )
    }

    @Test
    fun `returns NoConnectivity and does not upload when there is no network`() = runTest {
        every { connectivity.isConnected() } returns false

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoConnectivity)
        coVerify(exactly = 0) { remote.uploadPending(any(), any(), any(), any()) }
    }

    @Test
    fun `syncs fifo oldest first and deletes row and photo on http success`() = runTest {
        val older = pending(localId = "older", createdAt = 10L)
        val newer = pending(localId = "newer", createdAt = 20L)
        coEvery { pendingRepository.getPendingFifo() } returns listOf(older, newer)
        coEvery { remote.uploadPending(any(), any(), any(), any()) } returns SubscriptionSyncOutcome.Success
        every { photoStorage.fileFor("older") } returns File("/files/pending_subscriptions/older.jpg")
        every { photoStorage.fileFor("newer") } returns File("/files/pending_subscriptions/newer.jpg")

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().syncedCount)
        assertEquals(0, result.getOrThrow().failedCount)
        coVerifyOrder {
            pendingRepository.markSyncing("older")
            remote.uploadPending(
                older.subscriptionJson,
                older.clientRequestId,
                older.installationOrderId,
                any()
            )
            pendingRepository.deleteByLocalId("older")
            photoStorage.delete("older")
            pendingRepository.markSyncing("newer")
            remote.uploadPending(
                newer.subscriptionJson,
                newer.clientRequestId,
                newer.installationOrderId,
                any()
            )
            pendingRepository.deleteByLocalId("newer")
            photoStorage.delete("newer")
        }
    }

    @Test
    fun `marks CONFLICT and keeps row and photo on http 409`() = runTest {
        val item = pending(localId = "conflict-1", createdAt = 1L)
        coEvery { pendingRepository.getPendingFifo() } returns listOf(item)
        coEvery { remote.uploadPending(any(), any(), any(), any()) } returns SubscriptionSyncOutcome.Conflict
        every { photoStorage.fileFor(item.localId) } returns File("/files/pending_subscriptions/${item.localId}.jpg")

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().syncedCount)
        assertEquals(1, result.getOrThrow().failedCount)
        assertEquals(
            "HTTP 409: la suscripción ya existe en el servidor",
            result.getOrThrow().lastError
        )
        coVerify { pendingRepository.markSyncing(item.localId) }
        coVerify { pendingRepository.markConflict(item.localId, any()) }
        coVerify(exactly = 0) { pendingRepository.deleteByLocalId(any()) }
        coVerify(exactly = 0) { photoStorage.delete(any()) }
    }

    @Test
    fun `marks CONFLICT with actionable IP message and keeps row and photo on IP_CONFLICT`() = runTest {
        val item = pending(localId = "ip-conflict-1", createdAt = 1L)
        coEvery { pendingRepository.getPendingFifo() } returns listOf(item)
        coEvery { remote.uploadPending(any(), any(), any(), any()) } returns SubscriptionSyncOutcome.IpConflict
        every { photoStorage.fileFor(item.localId) } returns File("/files/pending_subscriptions/${item.localId}.jpg")

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().syncedCount)
        assertEquals(1, result.getOrThrow().failedCount)
        assertEquals(
            "IP ya en uso. Coordina otra IP con el equipo e intenta de nuevo.",
            result.getOrThrow().lastError
        )
        coVerify {
            pendingRepository.markConflict(
                item.localId,
                "IP ya en uso. Coordina otra IP con el equipo e intenta de nuevo."
            )
        }
        coVerify(exactly = 0) { pendingRepository.deleteByLocalId(any()) }
        coVerify(exactly = 0) { photoStorage.delete(any()) }
    }

    @Test
    fun `increments retryCount keeps PENDING on network error`() = runTest {
        val item = pending(localId = "retry-1", createdAt = 1L)
        coEvery { pendingRepository.getPendingFifo() } returns listOf(item)
        coEvery { remote.uploadPending(any(), any(), any(), any()) } returns
            SubscriptionSyncOutcome.Failure("timeout")
        every { photoStorage.fileFor(item.localId) } returns File("/files/pending_subscriptions/${item.localId}.jpg")

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().syncedCount)
        assertEquals(1, result.getOrThrow().failedCount)
        assertEquals("timeout", result.getOrThrow().lastError)
        coVerify { pendingRepository.markRetryableError(item.localId, "timeout") }
        coVerify(exactly = 0) { pendingRepository.deleteByLocalId(any()) }
        coVerify(exactly = 0) { photoStorage.delete(any()) }
        assertEquals(PendingSubscriptionStatus.PENDING, item.status)
    }

    @Test
    fun `forwards persisted clientIpAddress json to remote upload`() = runTest {
        val json = """{"firstName":"Ana","clientIpAddress":"192.168.1.77"}"""
        val item = pending(localId = "ip-1", createdAt = 1L).copy(subscriptionJson = json)
        coEvery { pendingRepository.getPendingFifo() } returns listOf(item)
        coEvery { remote.uploadPending(any(), any(), any(), any()) } returns SubscriptionSyncOutcome.Success
        every { photoStorage.fileFor(item.localId) } returns File("/files/pending_subscriptions/${item.localId}.jpg")

        val result = useCase()

        assertTrue(result.isSuccess)
        coVerify {
            remote.uploadPending(
                json,
                item.clientRequestId,
                item.installationOrderId,
                any()
            )
        }
    }

    @Test
    fun `empty queue returns syncedCount zero`() = runTest {
        coEvery { pendingRepository.getPendingFifo() } returns emptyList()

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().syncedCount)
        assertEquals(0, result.getOrThrow().failedCount)
        coVerify(exactly = 0) { remote.uploadPending(any(), any(), any(), any()) }
    }

    private fun pending(localId: String, createdAt: Long) = PendingSubscription(
        localId = localId,
        clientRequestId = "client-$localId",
        subscriptionJson = """{"dni":"$localId"}""",
        facadePhotoPath = "/files/pending_subscriptions/$localId.jpg",
        installationOrderId = 7,
        status = PendingSubscriptionStatus.PENDING,
        createdAt = createdAt
    )
}
