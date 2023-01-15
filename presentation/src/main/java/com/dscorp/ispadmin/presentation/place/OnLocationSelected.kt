package com.dscorp.ispadmin.presentation.place

import com.google.android.gms.maps.model.LatLng

interface MapLocationSelectionListener {
    fun onLocationSelected(latLng: LatLng)
}