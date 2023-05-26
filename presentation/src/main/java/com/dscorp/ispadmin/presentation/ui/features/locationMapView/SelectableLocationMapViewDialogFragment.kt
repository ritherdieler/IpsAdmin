package com.dscorp.ispadmin.presentation.ui.features.locationMapView

import android.annotation.SuppressLint
import android.view.View
import com.google.android.gms.maps.model.LatLng

class SelectableLocationMapViewDialogFragment(
    initialLocation: LatLng? = null,
    val callback: (LatLng) -> Unit = {}
) :LocationMapViewDialogFragment(initialLocation = initialLocation) {

    override fun afterBindingInflated() {
        binding.btnSelect.visibility = View.VISIBLE
        binding.btnSelect.setOnClickListener {
            callback.invoke(googleMap.cameraPosition.target)
            dismiss()
        }
    }

    @SuppressLint("MissingPermission")
    override fun  afterMapReady(){
        googleMap.isMyLocationEnabled = true
    }

}