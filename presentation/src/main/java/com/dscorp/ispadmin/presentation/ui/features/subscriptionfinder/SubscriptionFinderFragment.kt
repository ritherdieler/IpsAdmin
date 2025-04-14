package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.databinding.FragmentSubscriptionFinderBinding
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.SelectableLocationMapViewDialogFragment
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionFinderScreen
import com.google.android.gms.maps.model.LatLng

class SubscriptionFinderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSubscriptionFinderBinding.inflate(layoutInflater)

        val navController = findNavController()

        binding.root.setContent {
            SubscriptionFinderScreen(navController, onShowMapSelector = {
                activity?.supportFragmentManager?.let { fm ->
                    // Convert GeoLocation to LatLng before passing
                 it?.let {
                     val latLng = LatLng(it.latitude, it.longitude)
                     SelectableLocationMapViewDialogFragment(latLng).show(
                         fm, SelectableLocationMapViewDialogFragment::class.simpleName
                     )
                 }

                }
            })
        }
        return binding.root
    }

}
