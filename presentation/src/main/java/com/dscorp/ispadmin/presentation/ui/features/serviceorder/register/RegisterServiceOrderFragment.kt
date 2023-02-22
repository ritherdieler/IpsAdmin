package com.dscorp.ispadmin.presentation.ui.features.serviceorder.register

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

class RegisterServiceOrderFragment() : BaseFragment() {
    private val navArgs by navArgs<RegisterServiceOrderFragmentArgs>()
    private val binding by lazy { FragmentServiceOrderBinding.inflate(layoutInflater) }
    private val viewModel: RegisterServiceOrderViewModel by viewModel()
    private var selectedLocation: LatLng? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.subscription = navArgs.subscription
        observeServiceOrderResponse()
        observeServiceFormError()
        observeMapDialogResult()
        binding.etLocation.setOnClickListener {
            navigateToMapDialog()
        }

        probando()
        binding.btRegisterServiceOrder.setOnClickListener {
            registerServiceOrder()
        }
        return binding.root
    }

    private fun navigateToMapDialog() {
        findNavController().navigate(RegisterServiceOrderFragmentDirections.actionRegisterServiceOrderFragmentToMapDialog())
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
                is RegisterServiceOrderFormError.OnEtLocationError ->
                    binding.tlLocation.error =
                        error.error
                is RegisterServiceOrderFormError.OnSubscriptionError -> showErrorDialog()
                is RegisterServiceOrderFormError.GenericError -> showErrorDialog()
            }
        }
    }
    private fun probando(){
        val paymentMethods = resources.getStringArray(com.dscorp.ispadmin.R.array.prioridad)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, paymentMethods)
        binding.acPriority.setAdapter(adapter)
        binding.acPriority.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> binding.acPriority.setText("3")
                1 -> binding.acPriority.setText("2")
                2 -> binding.acPriority.setText("1")
            }
        }
    }
    private fun registerServiceOrder() {

        val serviceOrder = ServiceOrder(
            issue = binding.etIssue.text.toString(),
            latitude = selectedLocation?.latitude,
            longitude = selectedLocation?.longitude,
            subscriptionId = viewModel.subscription?.id,
            additionalDetails = binding.etAdditionalDetails.text.toString(),
            priority =  binding.acPriority.text.toString().toIntOrNull() ?: 0
        )
        viewModel.registerServiceOrder(serviceOrder)
    }

    private fun observeMapDialogResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("location")
            ?.observe(viewLifecycleOwner) { onLocationSelected(it) }
    }

    private fun onLocationSelected(location: LatLng?) {
        binding.etLocation.setText(location?.toStringLocation() ?: "")
        this.selectedLocation = location
    }
}
