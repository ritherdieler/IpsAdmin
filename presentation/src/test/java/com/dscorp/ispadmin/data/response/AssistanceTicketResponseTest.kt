package com.dscorp.ispadmin.data.response

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AssistanceTicketResponseTest {

    @Test
    fun `displayableIp returns the subscription ip when present`() {
        val ticket = ticket(ip = "192.168.30.10")
        assertEquals("192.168.30.10", ticket.displayableIp())
    }

    @Test
    fun `displayableIp is null when ip is missing or blank`() {
        assertNull(ticket(ip = null).displayableIp())
        assertNull(ticket(ip = "").displayableIp())
        assertNull(ticket(ip = "   ").displayableIp())
    }

    @Test
    fun `gson maps ip from assistance ticket json`() {
        val json = """
            {
              "id": 3213,
              "name": "PATRICIA CARILLO CUITANA",
              "phone": "946734696",
              "ip": "192.168.30.10",
              "category": "Sin Conexión a Internet",
              "description": "jakss",
              "status": "PENDING",
              "priority": "Alta",
              "place": "la villa"
            }
        """.trimIndent()

        val parsed = Gson().fromJson(json, AssistanceTicketResponse::class.java)

        assertEquals("192.168.30.10", parsed.ip)
        assertEquals("946734696", parsed.phone)
        assertEquals("la villa", parsed.place)
    }

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
