package com.dscorp.ispadmin.presentation.ui.features.supportTicket.list.compose

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.dscorp.ispadmin.data.response.AssistanceTicketResponse
import com.dscorp.ispadmin.data.response.AssistanceTicketStatus
import com.dscorp.ispadmin.domain.model.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class TicketCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows ip below phone when the ticket has a subscription ip`() {
        composeRule.setContent {
            MaterialTheme {
                TicketCard(
                    ticket = ticket(ip = "192.168.30.10"),
                    currentUser = secretary()
                )
            }
        }

        composeRule.onNodeWithText("Teléfono").assertExists()
        composeRule.onNodeWithTag("ticket_ip", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("IP").assertExists()
        composeRule.onNodeWithText("192.168.30.10").assertExists()
        composeRule.onNodeWithText("Ubicación").assertExists()
    }

    @Test
    fun `does not show an empty ip row when the ticket has no ip`() {
        composeRule.setContent {
            MaterialTheme {
                TicketCard(
                    ticket = ticket(ip = null),
                    currentUser = secretary()
                )
            }
        }

        composeRule.onNodeWithTag("ticket_ip", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithText("IP").assertDoesNotExist()
        composeRule.onNodeWithText("Teléfono").assertExists()
        composeRule.onNodeWithText("Ubicación").assertExists()
    }

    private fun secretary() = User(
        id = 1,
        name = "Ana",
        lastName = "Secretaria",
        type = User.UserType.SECRETARY
    )

    private fun ticket(ip: String?) = AssistanceTicketResponse(
        id = 3213,
        name = "PATRICIA CARILLO CUITANA",
        phone = "946734696",
        ip = ip,
        category = "Sin Conexión a Internet",
        description = "jakss",
        status = AssistanceTicketStatus.PENDING,
        priority = "Alta",
        place = "la villa",
        sheetImageUrl = null
    )
}
