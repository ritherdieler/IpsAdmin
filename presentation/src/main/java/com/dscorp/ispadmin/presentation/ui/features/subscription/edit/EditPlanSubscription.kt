package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentEditPlanSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.extension.populate
import com.dscorp.ispadmin.presentation.extension.showCrossDialog
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlanSubscription : BaseFragment() {
    private val args by navArgs<EditPlanSubscriptionArgs>()
    private val binding by lazy { FragmentEditPlanSubscriptionBinding.inflate(layoutInflater) }
    private val viewModel: EditSubscriptionViewModel by viewModel()

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
                is EditSubscriptionUiState.EditSubscriptionSuccess -> showEditSuccessDialog()
                is EditSubscriptionUiState.FormDataError -> showErrorDialog(response.error)
                is EditSubscriptionUiState.EditSubscriptionError -> showErrorDialog(response.error)
            }
        }
    }

    private fun showEditSuccessDialog() {
        showCrossDialog(
            text = getString(R.string.editPlanSuccess), positiveButtonClickListener = {
                val action =
                    EditPlanSubscriptionDirections.actionEditSubscriptionFragmentToNavDashboard()
                findNavController().navigate(action)
            }
        )
    }

    private fun fillFormSpinners(response: EditSubscriptionUiState.EditFormDataFound) {
        binding.acPlan.populate(response.plans) { plan ->
            viewModel.planField.value = plan
        }

        val selectedPlanId = viewModel.subscription?.plan?.id
        val selectedPlan = response.plans.find { it.id == selectedPlanId }
        viewModel.planField.value = selectedPlan
        binding.acPlan.setText(selectedPlan.toString(), false)
    }
}

