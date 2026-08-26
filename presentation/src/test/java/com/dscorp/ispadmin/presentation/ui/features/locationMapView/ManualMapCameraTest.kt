package com.dscorp.ispadmin.presentation.ui.features.locationMapView

import com.dscorp.ispadmin.domain.model.GeoLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ManualMapCameraTest {

    @Test
    fun `uses previously selected location when present`() {
        val target = initialMapCameraLatLng(
            GeoLocation(latitude = -10.5, longitude = -76.2)
        )

        assertEquals(-10.5, target.latitude, 0.000001)
        assertEquals(-76.2, target.longitude, 0.000001)
    }

    @Test
    fun `defaults camera to irrigacion santa rosa when there is no previous location`() {
        val target = initialMapCameraLatLng(null)

        assertEquals(-11.23416, target.latitude, 0.000001)
        assertEquals(-77.37872, target.longitude, 0.000001)
        assertEquals(16f, DEFAULT_MANUAL_MAP_CAMERA_ZOOM, 0.0f)
    }

    @Test
    fun `default camera is not lima`() {
        val target = initialMapCameraLatLng(null)

        assertNotEquals(-12.046374, target.latitude)
        assertNotEquals(-77.042793, target.longitude)
    }
}
