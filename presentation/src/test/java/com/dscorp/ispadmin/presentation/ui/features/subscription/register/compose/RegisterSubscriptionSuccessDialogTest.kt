package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.dscorp.ispadmin.domain.model.InstallationType
import com.dscorp.ispadmin.domain.model.Subscription
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class RegisterSubscriptionSuccessDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `COMPLETE shows automatic tr069 message and ssids`() {
        composeRule.setContent {
            MaterialTheme {
                Tr069StatusCard(
                    subscription = Subscription(
                        firstName = "Ana",
                        lastName = "García",
                        installationType = InstallationType.FIBER,
                        tr069ProvisionStatus = "COMPLETE",
                        wifiSsid24 = "Casa24",
                        wifiSsid5 = "Casa5"
                    )
                )
            }
        }

        composeRule.onNodeWithTag("tr069_status_card").assertIsDisplayed()
        composeRule.onNodeWithText(
            "ONU configurada automáticamente por TR-069. No requiere configuración manual."
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Casa24").assertIsDisplayed()
        composeRule.onNodeWithText("Casa5").assertIsDisplayed()
    }

    @Test
    fun `MANUAL_REQUIRED shows manual message reason and credentials`() {
        composeRule.setContent {
            MaterialTheme {
                Tr069StatusCard(
                    subscription = Subscription(
                        firstName = "Ana",
                        lastName = "García",
                        installationType = InstallationType.FIBER,
                        tr069ProvisionStatus = "MANUAL_REQUIRED",
                        tr069RequiresManualConfig = true,
                        tr069Message = "ONU no contactó al ACS",
                        wifiSsid24 = "Casa24",
                        wifiPassword24 = "clave24xx",
                        wifiSsid5 = "Casa5",
                        wifiPassword5 = "clave5xxx"
                    )
                )
            }
        }

        composeRule.onNodeWithTag("tr069_status_card").assertIsDisplayed()
        composeRule.onNodeWithText(
            "No se pudo configurar la ONU por TR-069. Configure la ONU manualmente."
        ).assertIsDisplayed()
        composeRule.onNodeWithText("ONU no contactó al ACS").assertIsDisplayed()
        composeRule.onNodeWithText("Casa24").assertIsDisplayed()
        composeRule.onNodeWithText("clave24xx").assertIsDisplayed()
    }

    @Test
    fun `MANUAL_REQUIRED shows retry button`() {
        composeRule.setContent {
            MaterialTheme {
                SuccessDialog(
                    subscription = Subscription(
                        subscriptionId = 42,
                        firstName = "Ana",
                        lastName = "García",
                        installationType = InstallationType.FIBER,
                        tr069ProvisionStatus = "MANUAL_REQUIRED",
                    ),
                    onRetryTr069 = {},
                    onDismiss = {},
                    onContinue = {}
                )
            }
        }

        composeRule.onNodeWithTag("btn_retry_tr069").assertIsDisplayed()
    }

    @Test
    fun `COMPLETE hides retry button`() {
        composeRule.setContent {
            MaterialTheme {
                SuccessDialog(
                    subscription = Subscription(
                        subscriptionId = 42,
                        firstName = "Ana",
                        lastName = "García",
                        installationType = InstallationType.FIBER,
                        tr069ProvisionStatus = "COMPLETE",
                    ),
                    onRetryTr069 = {},
                    onDismiss = {},
                    onContinue = {}
                )
            }
        }

        composeRule.onNodeWithTag("btn_retry_tr069").assertDoesNotExist()
    }

    @Test
    fun `NA hides tr069 card in SuccessDialog`() {
        composeRule.setContent {
            MaterialTheme {
                SuccessDialog(
                    subscription = Subscription(
                        firstName = "Ana",
                        lastName = "García",
                        dni = "12345678",
                        phone = "987654321",
                        address = "Av. Principal 100",
                        installationType = InstallationType.FIBER,
                        tr069ProvisionStatus = "NA"
                    ),
                    onDismiss = {},
                    onContinue = {}
                )
            }
        }

        composeRule.onNodeWithTag("tr069_status_card").assertDoesNotExist()
        composeRule.onNodeWithTag("register_success_message").assertIsDisplayed()
    }
}
