package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterSubscriptionProgressStepsTest {

    @Test
    fun `returns registering step at start`() {
        assertEquals("Registrando…", registrationProgressStepMessage(0L))
        assertEquals("Registrando…", registrationProgressStepMessage(14_999L))
    }

    @Test
    fun `returns authorizing onu step after 15s`() {
        assertEquals("Autorizando ONU…", registrationProgressStepMessage(15_000L))
        assertEquals("Autorizando ONU…", registrationProgressStepMessage(39_999L))
    }

    @Test
    fun `returns waiting acs step after 40s`() {
        assertEquals("Esperando ACS…", registrationProgressStepMessage(40_000L))
        assertEquals("Esperando ACS…", registrationProgressStepMessage(89_999L))
    }

    @Test
    fun `returns applying wifi step after 90s`() {
        assertEquals("Aplicando WiFi…", registrationProgressStepMessage(90_000L))
        assertEquals("Aplicando WiFi…", registrationProgressStepMessage(120_000L))
    }
}
