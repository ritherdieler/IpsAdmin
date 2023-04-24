package com.dscorp.ispadmin.presentation.util

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.DialogMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

/**
 * Created by Sergio Carrillo Diestra on 15/01/2023.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class MapDialog : DialogFragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var selectedLatLng: LatLng

    lateinit var binding: DialogMapBinding

    override fun getTheme(): Int = R.style.Theme_IspAdminAndroid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.btn.setOnClickListener {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("location", selectedLatLng)
            dismiss()
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        val santaRosa = LatLng(-11.234169, -77.378485)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santaRosa, 14f))
        selectedLatLng = santaRosa
        googleMap.setOnCameraMoveListener {
            selectedLatLng = googleMap.cameraPosition.target
            if (this@MapDialog::selectedLatLng.isInitialized) {
                binding.btn.isEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
