package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionDetailBinding
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.extension.toGeoLocation
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionDetailFragment :
    BaseFragment<SubscriptionDetailUiState, FragmentSubscriptionDetailBinding>() {

    private val args: SubscriptionDetailFragmentArgs by navArgs()

    override val viewModel: SubscriptionDetailViewModel by viewModel()
    override val binding by lazy { FragmentSubscriptionDetailBinding.inflate(layoutInflater) }

    override fun handleState(state: SubscriptionDetailUiState) {
        when (state) {
            SubscriptionDetailUiState.SubscriptionUpdated -> showSuccessDialog(R.string.subscription_updated)
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        viewModel.initForm(args.subscription)
        binding.viewModel = viewModel
        binding.executePendingBindings()


        binding.etLocation.setOnClickListener {
            viewModel.subscriptionForm.locationField.getValue()?.let {
                findNavController().navigate(
                    SubscriptionDetailFragmentDirections.actionSubscriptionDetailToMapView(
                        it.toGeoLocation()
                    )
                )
            }

        }
    }
}
