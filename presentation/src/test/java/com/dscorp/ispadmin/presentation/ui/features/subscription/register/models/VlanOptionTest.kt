package com.dscorp.ispadmin.presentation.ui.features.subscription.register.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VlanOptionTest {

    @Test
    fun `default registration vlan is 100`() {
        assertEquals("100", DEFAULT_REGISTRATION_VLAN)
    }

    @Test
    fun `vlan 1 is not selectable for registration`() {
        assertFalse(isRegistrationVlanSelectable("1"))
        assertTrue(isRegistrationVlanSelectable("100"))
    }

    @Test
    fun `vlan options mark vlan 1 disabled`() {
        val vlan1 = VLAN_OPTIONS.first { it.value == "1" }
        val vlan100 = VLAN_OPTIONS.first { it.value == "100" }

        assertFalse(vlan1.selectable)
        assertTrue(vlan100.selectable)
    }
}
