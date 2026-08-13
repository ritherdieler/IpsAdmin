package com.dscorp.ispadmin.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.dscorp.ispadmin.data.local.IspAdminDatabase
import com.dscorp.ispadmin.data.local.entity.PendingSubscriptionEntity
import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PendingSubscriptionDaoTest {

    private lateinit var database: IspAdminDatabase
    private lateinit var dao: PendingSubscriptionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            IspAdminDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.pendingSubscriptionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert then getAllFifo returns persisted row`() = runTest {
        val entity = pendingEntity(localId = "local-a", createdAt = 100L)

        dao.insert(entity)

        val result = dao.getAllFifo()
        assertEquals(1, result.size)
        assertEquals("local-a", result.first().localId)
        assertEquals("req-local-a", result.first().clientRequestId)
        assertEquals("""{"dni":"111"}""", result.first().subscriptionJson)
        assertEquals(PendingSubscriptionStatus.PENDING, result.first().status)
    }

    @Test
    fun `getAllFifo returns oldest createdAt first`() = runTest {
        dao.insert(pendingEntity(localId = "newer", createdAt = 200L))
        dao.insert(pendingEntity(localId = "older", createdAt = 100L))

        val result = dao.getAllFifo()

        assertEquals(listOf("older", "newer"), result.map { it.localId })
    }

    @Test
    fun `deleteByLocalId removes only matching row`() = runTest {
        dao.insert(pendingEntity(localId = "keep", createdAt = 1L))
        dao.insert(pendingEntity(localId = "drop", createdAt = 2L))

        dao.deleteByLocalId("drop")

        val result = dao.getAllFifo()
        assertEquals(listOf("keep"), result.map { it.localId })
    }

    private fun pendingEntity(
        localId: String,
        createdAt: Long
    ) = PendingSubscriptionEntity(
        localId = localId,
        clientRequestId = "req-$localId",
        subscriptionJson = """{"dni":"111"}""",
        facadePhotoPath = "/files/pending_subscriptions/$localId.jpg",
        installationOrderId = 7,
        status = PendingSubscriptionStatus.PENDING,
        createdAt = createdAt,
        lastError = null,
        retryCount = 0
    )
}
