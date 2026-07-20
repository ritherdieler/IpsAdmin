package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import com.dscorp.ispadmin.domain.model.NetworkDevice
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.models.RegisterSubscriptionFormState
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RegisterSubscriptionFormHostDeviceTest {

    @Test
    fun `InstallationBlock muestra dropdown host cuando hay mas de un core activo`() {
        val formSource = File(
            "src/main/java/com/dscorp/ispadmin/presentation/ui/features/subscription/register/compose/RegisterSubscriptionForm.kt"
        ).readText()

        assertThat(formSource).contains("form.shouldShowHostDeviceSelector()")
        assertThat(formSource).contains("testTag(\"register_host_device_dropdown\")")
        assertThat(formSource).contains("RegisterSubscriptionIntent.HostDeviceSelected")
        assertThat(formSource).contains("R.string.host_device")
        assertThat(formSource).contains("form.activeCoreDevices()")
    }

    @Test
    fun `shouldShowHostDeviceSelector refleja visibilidad del dropdown`() {
        val oneCore = RegisterSubscriptionFormState(
            coreDeviceList = listOf(NetworkDevice(id = 1, name = "A", disabled = false))
        )
        val twoCores = RegisterSubscriptionFormState(
            coreDeviceList = listOf(
                NetworkDevice(id = 1, name = "A", disabled = false),
                NetworkDevice(id = 2, name = "B", disabled = false)
            )
        )

        assertFalse(oneCore.shouldShowHostDeviceSelector())
        assertTrue(twoCores.shouldShowHostDeviceSelector())
    }
}
