package com.dscorp.ispadmin.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.dscorp.ispadmin.data.local.IspAdminDatabase
import com.dscorp.ispadmin.domain.model.PendingSubscription
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import com.dscorp.ispadmin.domain.repository.PendingSubscriptionRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PendingSubscriptionRepositoryImplTest {

    private lateinit var database: IspAdminDatabase
    private lateinit var repository: PendingSubscriptionRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            IspAdminDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = PendingSubscriptionRepositoryImpl(database.pendingSubscriptionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `enqueue persists pending subscription for fifo query`() = runTest {
        val pending = pendingSubscription(localId = "local-1", createdAt = 50L)

        val result = repository.enqueue(pending)

        assertTrue(result.isSuccess)
        val stored = repository.getPendingFifo()
        assertEquals(1, stored.size)
        assertEquals(pending, stored.first())
        assertTrue(stored.first().subscriptionJson.contains("clientIpAddress"))
        assertTrue(stored.first().subscriptionJson.contains("192.168.1.77"))
    }

    @Test
    fun `getPendingFifo returns oldest createdAt first`() = runTest {
        repository.enqueue(pendingSubscription(localId = "second", createdAt = 20L))
        repository.enqueue(pendingSubscription(localId = "first", createdAt = 10L))

        val stored = repository.getPendingFifo()

        assertEquals(listOf("first", "second"), stored.map { it.localId })
    }

    @Test
    fun `deleteByLocalId removes enqueued subscription after simulated sync`() = runTest {
        repository.enqueue(pendingSubscription(localId = "synced", createdAt = 1L))
        repository.enqueue(pendingSubscription(localId = "still-pending", createdAt = 2L))

        val result = repository.deleteByLocalId("synced")

        assertTrue(result.isSuccess)
        assertEquals(listOf("still-pending"), repository.getPendingFifo().map { it.localId })
    }

    private fun pendingSubscription(
        localId: String,
        createdAt: Long
    ) = PendingSubscription(
        localId = localId,
        clientRequestId = "client-$localId",
        subscriptionJson = """{"dni":"999","clientIpAddress":"192.168.1.77"}""",
        facadePhotoPath = "/files/pending_subscriptions/$localId.jpg",
        installationOrderId = 3,
        status = PendingSubscriptionStatus.PENDING,
        createdAt = createdAt,
        lastError = null,
        retryCount = 0
    )
}
