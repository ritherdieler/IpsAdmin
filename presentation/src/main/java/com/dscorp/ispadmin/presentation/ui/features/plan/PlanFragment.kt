package com.dscorp.ispadmin.presentation.ui.features.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentPlanBinding
import com.dscorp.ispadmin.presentation.util.IDialogFactory
import com.dscorp.ispadmin.presentation.ui.features.plan.PlanFormError.*
import com.example.cleanarchitecture.domain.domain.entity.Plan
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlanFragment : Fragment(R.layout.fragment_plan) {
    lateinit var binding: FragmentPlanBinding
    val dialogFactory: IDialogFactory by inject()

    val viewModel: PlanViewModel by viewModel()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_plan, null, true)
        observePlanResponse()
        observeFormError()

        binding.btRegisterPlan.setOnClickListener {
            registerPlan()
        }

        return binding.root
    }

    private fun observePlanResponse() {
        viewModel.planResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is PlanResponse.OnError -> showErrorDialog(response.error.message.toString())
                is PlanResponse.OnPlanRegistered -> showSuccessDialog(response.plan.name)
            }
        }
    }

    private fun observeFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtDowloadSpeedError -> setErrorEtDowloadSpeed(formError)
                is OnEtNamePlanError -> setErrorEtNamePlan(formError)
                is OnEtPriceError -> setErrorEtPrice(formError)
                is OnEtUploadSpeedError -> setErrorEtUploadSpeed(formError)
            }
        }
    }

    private fun setErrorEtUploadSpeed(formError: OnEtUploadSpeedError) {
        binding.etUploadSpeed.setError((formError.error))
    }

    private fun setErrorEtPrice(formError: OnEtPriceError) {
        binding.etPrice.setError(formError.error)
    }

    private fun setErrorEtNamePlan(formError: OnEtNamePlanError) {
        binding.etNamePlan.setError(formError.error)
    }

    private fun setErrorEtDowloadSpeed(formError: OnEtDowloadSpeedError) {
        binding.etDownloadSpeed.setError(formError.error)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showSuccessDialog(plan: String) {
        val message = "el plan $plan ah sido procesado correctamente"
        dialogFactory.createSuccessDialog(requireContext(), message).show()
    }

    private fun showErrorDialog(error: String) {
        val errorDialog = dialogFactory.createErrorDialog(requireContext())
        errorDialog.show()
    }

    private fun registerPlan() {
        val plan = Plan(
            name = binding.etNamePlan.text.toString(),
            price = binding.etPrice.text.toString().toDouble(),
            downloadSpeed = binding.etDownloadSpeed.text.toString(),
            uploadSpeed = binding.etUploadSpeed.text.toString()
        )
        viewModel.registerPlan(plan)
    }
}