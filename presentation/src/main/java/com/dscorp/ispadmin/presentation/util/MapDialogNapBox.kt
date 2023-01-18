package com.dscorp.ispadmin.presentation.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.DialogMapBinding
import com.dscorp.ispadmin.databinding.DialogMapNapBoxBinding
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
class MapDialogNapBox : DialogFragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var selectedLatLng: LatLng
    lateinit var binding: DialogMapNapBoxBinding

    override fun getTheme(): Int = R.style.Theme_IspAdminAndroid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_map_nap_box, null, true)

/*         es de prueba a ver si mas adelante ada algun error lo cambiamos por lo comentado
        binding = DialogMapNapBoxBinding.inflate(inflater, container, false)*/

        mapView = binding.mapViewNapBox
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.btnConfirmar.setOnClickListener {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("location", selectedLatLng)
            dismiss()
        }
        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val santaRosa = LatLng(-11.234996, -77.380347)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(santaRosa))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11.5f))

        googleMap.setOnCameraMoveListener {
            selectedLatLng = googleMap.cameraPosition.target
            if (this@MapDialogNapBox::selectedLatLng.isInitialized) {
                binding.btnConfirmar.isEnabled = true
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