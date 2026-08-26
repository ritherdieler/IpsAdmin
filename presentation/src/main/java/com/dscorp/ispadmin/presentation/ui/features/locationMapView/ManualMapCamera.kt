package com.dscorp.ispadmin.presentation.ui.features.locationMapView

import com.dscorp.ispadmin.domain.model.GeoLocation
import com.google.android.gms.maps.model.LatLng

/** Irrigación Santa Rosa - La Villa, Sayán. Camera-only default; not an auto-selected location. */
internal val DEFAULT_MANUAL_MAP_CAMERA_TARGET = LatLng(-11.23416, -77.37872)
internal const val DEFAULT_MANUAL_MAP_CAMERA_ZOOM = 16f

internal fun initialMapCameraLatLng(initialLocation: GeoLocation?): LatLng {
    return initialLocation?.let { LatLng(it.latitude, it.longitude) }
        ?: DEFAULT_MANUAL_MAP_CAMERA_TARGET
}
