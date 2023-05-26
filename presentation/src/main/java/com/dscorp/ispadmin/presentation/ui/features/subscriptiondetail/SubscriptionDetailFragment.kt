package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.ReadOnlyLocationMapViewDialogFragment
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.SelectableLocationMapViewDialogFragment
import com.dscorp.ispadmin.presentation.util.PermissionManager
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionDetailFragment :
    BaseFragment<SubscriptionDetailUiState, FragmentSubscriptionDetailBinding>() {

    private val args: SubscriptionDetailFragmentArgs by navArgs()

    override val viewModel: SubscriptionDetailViewModel by viewModel()
    override val binding by lazy { FragmentSubscriptionDetailBinding.inflate(layoutInflater) }

    lateinit var permissionManager: PermissionManager

    override fun handleState(state: SubscriptionDetailUiState) {
        when (state) {
            SubscriptionDetailUiState.SubscriptionUpdated -> showSuccessDialog(R.string.subscription_updated) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        permissionManager = PermissionManager(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        viewModel.initForm(args.subscription)

        binding.etLocation.setOnClickListener {
            viewModel.editSubscriptionForm.locationField.getValue()?.let {
                permissionManager.requestPermission(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    onGranted = {
                        showLocationMapDialog(it)
                    })
            }

        }
    }

    private fun showLocationMapDialog(it: LatLng) {
        if (viewModel.isEditingForm)
            SelectableLocationMapViewDialogFragment(it) {
                viewModel.editSubscriptionForm.locationField.setValue(it)
            }.show(childFragmentManager, "map")
        else
            ReadOnlyLocationMapViewDialogFragment(it).show(childFragmentManager, "map")

    }
}
