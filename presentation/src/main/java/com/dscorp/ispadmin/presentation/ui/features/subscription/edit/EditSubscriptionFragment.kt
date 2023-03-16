package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentEditSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.extension.populate
import com.dscorp.ispadmin.presentation.extension.showCrossDialog
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditSubscriptionFragment : BaseFragment() {
    private val args by navArgs<EditSubscriptionFragmentArgs>()
    private val binding by lazy { FragmentEditSubscriptionBinding.inflate(layoutInflater) }
    private val viewModel: EditSubscriptionViewModel by viewModel()
    var selectedPlan: PlanResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.subscription = args.subscription
        fillFormWithSubscriptionData()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        viewModel.getFormData()
        observeState()

        binding.btnEditSubscription.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            viewModel.editarSubscription()
        }

        return binding.root
    }

    private fun fillFormWithSubscriptionData() {
        binding.etFirstName.setText(viewModel.subscription?.firstName)
        binding.etLastName.setText(viewModel.subscription?.lastName)
        binding.etDni.setText(viewModel.subscription?.dni)
    }

    private fun observeState() = lifecycleScope.launch {
        viewModel.editSubscriptionUiState.collect { response ->
            when (response) {

                is EditSubscriptionUiState.EditFormDataFound -> fillFormSpinners(response)
                is EditSubscriptionUiState.EditSubscriptionSuccess -> showCrossDialog(
                    text = getString(
                        R.string.subscription_register_success,
                        response.subscription.ip.toString()
                    ), positiveButtonClickListener = {
                        val navigate = EditSubscriptionFragmentDirections.actionEditSubscriptionFragmentToNavDashboard()
                        findNavController().navigate(navigate)
                    }
                )
                is EditSubscriptionUiState.FormDataError -> showErrorDialog(response.error)
                is EditSubscriptionUiState.EditSubscriptionError -> showErrorDialog(response.error)
            }
        }
    }

    private fun fillFormSpinners(response: EditSubscriptionUiState.EditFormDataFound) {
        binding.etPlan.populate(response.plans) { plan ->
            viewModel.planField.value = plan
        }
        val selectedPlanId = viewModel.subscription?.plan?.id
        selectedPlan = response.plans.find { it.id == selectedPlanId }
        selectedPlan?.let {
            binding.etPlan.setSelection(response.plans.indexOf(it))
            binding.etPlan.setText(it.name)
            viewModel.planField.value = it
        }
    }
}

