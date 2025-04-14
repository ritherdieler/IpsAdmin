package com.dscorp.ispadmin.presentation.ui.features.locationMapView

import android.annotation.SuppressLint
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.gms.maps.model.LatLng

// Define a key for the result
const val MAP_SELECTION_REQUEST_KEY = "mapSelectionRequest"
const val MAP_SELECTION_RESULT_KEY = "selectedLocation"

class SelectableLocationMapViewDialogFragment(
    initialLocation: LatLng? = null,
) : LocationMapViewDialogFragment(initialLocation = initialLocation) {

    override fun afterBindingInflated() {
        binding.btnSelectLocation.visibility = View.VISIBLE
        binding.btnSelectLocation.setOnClickListener {
            // Set the result using the Fragment Result API
            setFragmentResult(MAP_SELECTION_REQUEST_KEY, bundleOf(MAP_SELECTION_RESULT_KEY to googleMap.cameraPosition.target))
            dismiss()
        }
    }

    @SuppressLint("MissingPermission")
    override fun afterMapReady(){
        googleMap.isMyLocationEnabled = true
    }

}