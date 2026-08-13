package com.dscorp.ispadmin.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PendingSubscriptionTest {

    @Test
    fun `new pending subscription starts as PENDING with zero retries`() {
        val pending = PendingSubscription(
            localId = "local-1",
            clientRequestId = "client-1",
            subscriptionJson = """{"dni":"12345678"}""",
            createdAt = 1_000L
        )

        assertEquals(PendingSubscriptionStatus.PENDING, pending.status)
        assertEquals(0, pending.retryCount)
        assertNull(pending.lastError)
        assertNull(pending.facadePhotoPath)
        assertNull(pending.installationOrderId)
    }
}
