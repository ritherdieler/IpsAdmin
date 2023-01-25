package com.dscorp.ispadmin.presentation.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ViewMapBinding
import com.dscorp.ispadmin.presentation.subscriptiondetail.SubscriptionDetailFragmentArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by Sergio Carrillo Diestra on 15/01/2023.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class MapViewDialogFragment : DialogFragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private  val args: MapViewDialogFragmentArgs by navArgs()

    lateinit var binding: ViewMapBinding

    override fun getTheme(): Int = R.style.Theme_IspAdminAndroid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView2
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val location = LatLng(args.location?.latitude?:0.0,args.location?.longitude?:0.0)
        googleMap.addMarker(MarkerOptions()
            .position(location)
            .title("HOLAAAAAAAAAAA "))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f))
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