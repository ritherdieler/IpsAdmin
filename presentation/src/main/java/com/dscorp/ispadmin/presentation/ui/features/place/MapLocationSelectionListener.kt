package com.dscorp.ispadmin.presentation.ui.features.place

import android.view.View
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.google.android.gms.maps.model.LatLng

interface MapLocationSelectionListener {
    fun onLocationSelected(latLng: LatLng)

}
