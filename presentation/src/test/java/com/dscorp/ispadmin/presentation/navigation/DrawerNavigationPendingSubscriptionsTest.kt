package com.dscorp.ispadmin.presentation.navigation

import com.dscorp.ispadmin.domain.model.User
import com.dscorp.ispadmin.navigation.NavRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawerNavigationPendingSubscriptionsTest {

    @Test
    fun `technician drawer includes pending subscriptions item`() {
        val items = DrawerNavigation.getDrawerGroupsForUser(User.UserType.TECHNICIAN)
            .flatMap { it.items }

        val pending = items.first { it.title == "Suscripciones pendientes" }
        assertEquals(NavRoutes.FeatureRoutes.Subscription.PendingSubscriptions, pending.route)
        assertTrue(items.any { it.title == "Registrar suscripción" })
    }
}
