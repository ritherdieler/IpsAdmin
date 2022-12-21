package com.dscorp.ispadmin.presentation.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentPlanBinding
import com.dscorp.ispadmin.presentation.plan.PlanFormError.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlanFragment : Fragment() {
    lateinit var binding : FragmentPlanBinding
    val viewModel: PlanViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_plan,null,true)
        observePlanResponse()
        observeFormError()

        binding.btRegisterPlan.setOnClickListener{
            registerPlan()
        }
        return binding.root
    }

    private fun observePlanResponse() { viewModel.planResponseLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is PlanResponse.OnError ->showErrorDialog(response.error.message.toString())
                is PlanResponse.OnPlanRegistered ->showSucessDialog(response.plan.name)
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

    fun showSucessDialog(plan: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Plan Registrado con Exito")
        builder.setMessage(plan)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El plan no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    fun registerPlan() {
        val namePlan = binding.etNamePlan.text.toString()
        val precio = binding.etPrice.text.toString()
        val downloadSpeed = binding.etDownloadSpeed.text.toString()
        val uploadSpeed = binding.etUploadSpeed.text.toString()
        viewModel.registerPlan(namePlan =namePlan, precio = precio, downloadSpeed = downloadSpeed, uploadSpeed =uploadSpeed)
    }


}