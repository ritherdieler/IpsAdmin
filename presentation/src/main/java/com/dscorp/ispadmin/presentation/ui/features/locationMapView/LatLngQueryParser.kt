package com.dscorp.ispadmin.presentation.ui.features.locationMapView

import com.dscorp.ispadmin.domain.model.GeoLocation

private val LAT_LNG_QUERY_REGEX = Regex(
    """^\s*(-?\d+(?:\.\d+)?)\s*,\s*(-?\d+(?:\.\d+)?)\s*$"""
)

fun parseLatLngQuery(raw: String): GeoLocation? {
    val match = LAT_LNG_QUERY_REGEX.matchEntire(raw) ?: return null
    val latitude = match.groupValues[1].toDoubleOrNull() ?: return null
    val longitude = match.groupValues[2].toDoubleOrNull() ?: return null
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
    return GeoLocation(latitude = latitude, longitude = longitude)
}
