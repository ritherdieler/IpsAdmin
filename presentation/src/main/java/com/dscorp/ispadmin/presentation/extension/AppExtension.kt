package com.dscorp.ispadmin.presentation.extension

import androidx.navigation.NavController
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.google.android.gms.maps.model.LatLng


fun NavController.navigateSafe(destinationId: Int) {
    try {
        navigate(destinationId)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun LatLng.toGeoLocation(): GeoLocation = GeoLocation(latitude, longitude)
