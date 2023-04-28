package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
import com.example.cleanarchitecture.domain.domain.entity.User
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlanSubscription : BaseFragment<EditSubscriptionUiState,FragmentEditPlanSubscriptionBinding >() {
    private val args by navArgs<EditPlanSubscriptionArgs>()
    override val binding by lazy { FragmentEditPlanSubscriptionBinding.inflate(layoutInflater) }
    override val viewModel: EditSubscriptionViewModel by viewModels()

    override fun handleState(state: EditSubscriptionUiState) {
        when (state) {
            is EditSubscriptionUiState.EditFormDataFound -> fillFormSpinners(state)
            is EditSubscriptionUiState.EditSubscriptionSuccess -> showEditSuccessDialog()
            is EditSubscriptionUiState.FormDataError -> showErrorDialog(state.error)
            is EditSubscriptionUiState.EditSubscriptionError -> showErrorDialog(state.error)
        }
    }

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

        binding.btnEditSubscription.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            viewModel.editSubscription()
        }

        return binding.root
    }

    private fun fillFormWithSubscriptionData() {
        binding.etFirstName.setText(viewModel.subscription?.firstName)
        binding.etLastName.setText(viewModel.subscription?.lastName)
        binding.etDni.setText(viewModel.subscription?.dni)


    }

    private fun showEditSuccessDialog() {
        showCrossDialog(
            text = getString(R.string.editPlanSuccess), positiveButtonClickListener = {
                if (viewModel.user!!.type == User.UserType.ADMIN) {
                    val action =
                        EditPlanSubscriptionDirections.actionEditSubscriptionFragmentToNavDashboard()
                    findNavController().navigate(action)
                } else {
                    val action =
                        EditPlanSubscriptionDirections.editSubscriptionPlanToFindSubscription()
                    findNavController().navigate(action)
                }

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

