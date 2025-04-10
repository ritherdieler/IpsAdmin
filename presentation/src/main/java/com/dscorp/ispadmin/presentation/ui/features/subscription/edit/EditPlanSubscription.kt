package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import android.os.Bundle
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
import com.example.cleanarchitecture.domain.entity.SubscriptionResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlanSubscription :
    BaseFragment<EditSubscriptionUiState, FragmentEditPlanSubscriptionBinding>() {
    private val args by navArgs<EditPlanSubscriptionArgs>()
    override val binding by lazy { FragmentEditPlanSubscriptionBinding.inflate(layoutInflater) }
    override val viewModel: EditSubscriptionViewModel by viewModel()

    override fun handleState(state: EditSubscriptionUiState) {
        when (state) {
            is EditSubscriptionUiState.EditFormDataFound -> fillForm(state)
            is EditSubscriptionUiState.EditSubscriptionSuccess -> showEditSuccessDialog()
            is EditSubscriptionUiState.FormDataError -> showErrorDialog(state.error)
            is EditSubscriptionUiState.EditSubscriptionError -> showErrorDialog(state.error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        viewModel.getFormData(args.subscriptionId)
        binding.btnEditSubscription.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            viewModel.editSubscription(args.subscriptionId)
        }
        super.onCreate(savedInstanceState)
    }

    private fun fillFormWithSubscriptionData(subscription: SubscriptionResponse) {
        binding.etFirstName.setText(subscription.firstName)
        binding.etLastName.setText(subscription.lastName)
        binding.etDni.setText(subscription.dni)
    }

    private fun showEditSuccessDialog() {
        showCrossDialog(
            text = getString(R.string.editPlanSuccess), positiveButtonClickListener = {
                findNavController().navigateUp()
            }
        )
    }

    private fun fillForm(response: EditSubscriptionUiState.EditFormDataFound) {
        binding.acPlan.populate(response.plans) { plan ->
            viewModel.planField.value = plan
        }

        fillFormWithSubscriptionData(response.subscription)

        val selectedPlanId = response.subscription.plan?.id
        val selectedPlan = response.plans.find { it.id == selectedPlanId }
        viewModel.planField.value = selectedPlan
        binding.acPlan.setText(selectedPlan.toString(), false)
    }
}

