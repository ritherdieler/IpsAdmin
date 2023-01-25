package com.dscorp.ispadmin.presentation.payment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.databinding.FragmentRegisterPaymentBinding
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterPaymentFragment : Fragment() {

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
        binding.btnRegisterPayment.setOnClickListener {
            viewModel.registerPayment(
                Payment(
                    amountPaid = binding.etPaymentAmount.text.toString().toDouble(),
                    discount = binding.etPaymentDiscount.text.toString().toDouble(),
                    discountReason = binding.etPaymentDiscountReason.text.toString(),
                    method = binding.etPaymentMethod.text.toString()
                )
            )
        }

        viewModel.registerPaymentState.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterPaymentUiState.OnPaymentRegistered -> {
                    showMaterialSuccessDialog()
                }
                is RegisterPaymentUiState.OnError -> {
                    showMaterialErrorDialog(it.message)
                }
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