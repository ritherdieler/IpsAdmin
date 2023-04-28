package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentServiceOrderBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.extension.toStringLocation
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterServiceOrderFragment : Fragment() {
    private val navArgs by navArgs<RegisterServiceOrderFragmentArgs>()
    private val binding by lazy { FragmentServiceOrderBinding.inflate(layoutInflater) }
    private val viewModel: RegisterServiceOrderViewModel by viewModel()
    private var selectedPriority: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.subscription = navArgs.subscription
        observeServiceOrderResponse()
        observeServiceFormError()
        spinnerPriority()
        binding.btRegisterServiceOrder.setOnClickListener {
            registerServiceOrder()
        }

        binding.acPriority.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val priority = parent.getItemAtPosition(position) as String
                selectedPriority = when (priority) {
                    "Alto" -> 3
                    "Medio" -> 2
                    "Bajo" -> 1
                    else -> 1
                }
            }
        return binding.root
    }


    private fun observeServiceOrderResponse() {
        viewModel.uiState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is RegisterServiceOrderUiState.ServiceOrderRegisterErrorOrder -> showErrorDialog()
                is RegisterServiceOrderUiState.ServiceOrderRegisterSuccessOrder -> showSuccessDialog()
            }
        }
    }

    private fun showSuccessDialog() {
        showSuccessDialog("se ah registrado correctamente")
    }

    private fun observeServiceFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { error ->
            when (error) {
                is RegisterServiceOrderFormError.OnEtIssueError ->
                    binding.tlIssue.error =
                        error.error
                is RegisterServiceOrderFormError.OnSubscriptionError -> showErrorDialog()
                is RegisterServiceOrderFormError.GenericError -> showErrorDialog()
            }
        }
    }

    private fun spinnerPriority() {
        val paymentMethods = resources.getStringArray(com.dscorp.ispadmin.R.array.prioridad)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, paymentMethods)
        binding.acPriority.setAdapter(adapter)
    }

    private fun registerServiceOrder() {
        val serviceOrder = ServiceOrder(
            issue = binding.etIssue.text.toString(),
            subscriptionId = viewModel.subscription?.id,
            additionalDetails = binding.etAdditionalDetails.text.toString(),
            priority = selectedPriority,
            createdByUserId = viewModel.user?.id,
        )
        viewModel.registerServiceOrder(serviceOrder)
    }
}
