package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.ReadOnlyLocationMapViewDialogFragment
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.SelectableLocationMapViewDialogFragment
import com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail.compose.SubscriptionDetailForm
import com.dscorp.ispadmin.presentation.util.PermissionManager
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionDetailFragment :
    Fragment() {

        val args: SubscriptionDetailFragmentArgs by navArgs()

    val binding by lazy { FragmentSubscriptionDetailBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.root.setContent {
            SubscriptionDetailForm(args.subscriptionId)
        }

        return binding.root
    }
}
