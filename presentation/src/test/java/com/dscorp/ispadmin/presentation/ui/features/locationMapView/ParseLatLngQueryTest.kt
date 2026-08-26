package com.dscorp.ispadmin.presentation.ui.features.locationMapView

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParseLatLngQueryTest {

    @Test
    fun `parses comma separated latitude and longitude`() {
        val result = parseLatLngQuery("-12.046374, -77.042793")

        assertEquals(-12.046374, result!!.latitude, 0.000001)
        assertEquals(-77.042793, result.longitude, 0.000001)
    }

    @Test
    fun `parses values without spaces`() {
        val result = parseLatLngQuery("-11.1,-76.2")

        assertEquals(-11.1, result!!.latitude, 0.0001)
        assertEquals(-76.2, result.longitude, 0.0001)
    }

    @Test
    fun `returns null for empty or incomplete query`() {
        assertNull(parseLatLngQuery(""))
        assertNull(parseLatLngQuery("   "))
        assertNull(parseLatLngQuery("-12.04"))
        assertNull(parseLatLngQuery("abc, def"))
    }

    @Test
    fun `returns null when coordinates are out of range`() {
        assertNull(parseLatLngQuery("91.0, 0.0"))
        assertNull(parseLatLngQuery("0.0, 181.0"))
    }
}
