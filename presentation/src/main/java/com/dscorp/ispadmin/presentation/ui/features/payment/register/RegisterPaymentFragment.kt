package com.dscorp.ispadmin.presentation.ui.features.payment.register

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentRegisterPaymentBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterPaymentFragment : BaseFragment() {
    private val args: RegisterPaymentFragmentArgs by navArgs()
    private val viewModel: RegisterPaymentViewModel by viewModel()
    private val binding by lazy { FragmentRegisterPaymentBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.tvPlan.text = "Deuda: ${args.payment.amountPaidStr()}"

        val paymentMethods = resources.getStringArray(com.dscorp.ispadmin.R.array.payment_methods)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, paymentMethods)
        binding.acPaymentMethod.setAdapter(adapter)

        binding.btnRegisterPayment.setOnClickListener {
            val discount = if (binding.etPaymentDiscount.text.toString().isEmpty()) 0.0
            else binding.etPaymentDiscount.text.toString().toDouble()
            viewModel.registerPayment(
                Payment(
                    amountPaid = args.payment.amountPaid,
                    discountAmount = discount,
                    discountReason = binding.etPaymentDiscountReason.text.toString(),
                    method = binding.acPaymentMethod.text.toString(),
                )
            )
        }

        observeUiState()
        observeFormErrorUiState()
    }

    private fun observeFormErrorUiState() {
        viewModel.registerPaymentFormErrorState.observe(viewLifecycleOwner) {
            showMaterialErrorDialog(it.message)
        }
    }

    private fun observeUiState() {
        viewModel.registerPaymentState.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterPaymentUiState.OnPaymentRegistered -> showMaterialSuccessDialog()
                is RegisterPaymentUiState.OnError -> showMaterialErrorDialog(it.message)
            }
        }
    }

    private fun showMaterialErrorDialog(message: String?) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showMaterialSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pago registrado")
            .setMessage("El pago se ha registrado correctamente")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}