package com.dscorp.ispadmin.presentation.ui.features.payment.register

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentRegisterPaymentBinding
import com.dscorp.ispadmin.presentation.extension.populate
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterPaymentFragment :
    BaseFragment<RegisterPaymentUiState, FragmentRegisterPaymentBinding>() {
    private val args: RegisterPaymentFragmentArgs by navArgs()

    override val binding by lazy { FragmentRegisterPaymentBinding.inflate(layoutInflater) }
    override val viewModel: RegisterPaymentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.payment = args.payment
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setupView()
        viewModel.getElectronicPayers()
    }

    override fun handleState(state: RegisterPaymentUiState) =
        when (state) {
            is RegisterPaymentUiState.OnPaymentRegistered -> showSuccessDialog("Pago registrado") {
                findNavController().popBackStack()
            }

            is RegisterPaymentUiState.HideDiscountFields -> {
                binding.textView.text = getString(R.string.register_payment_is_by_reconnection)
                binding.tlPaymentDiscount.visibility = View.GONE
                binding.tlPaymentDiscountReason.visibility = View.GONE
            }

            is RegisterPaymentUiState.OnElectronicPayersLoaded -> {
                val electronicPayers = state.electronicPayers

                if (electronicPayers.isNotEmpty()) {
                    binding.acElectronicPayerName.populate(electronicPayers) { electronicPayerName ->
                        viewModel.payment!!.electronicPayerName =
                            binding.acElectronicPayerName.text.toString()
                    }
                } else {

                }
            }
        }

    private fun setupView() {
        binding.tvPlan.text = "Deuda: ${args.payment.amountToPayStr()}"
        populatePaymentMethodSpinner()
        binding.acElectronicPayerName.addTextChangedListener {
            viewModel.payment!!.electronicPayerName = it.toString()
        }

        val discountReasons  = listOf("Fallas de internet", "Fallas de TVcable", "Error de facturacion")

        binding.acPaymentDiscountReason.populate(discountReasons) { }

    }


    private fun populatePaymentMethodSpinner() {
        val paymentMethods =
            resources.getStringArray(R.array.payment_methods).asList()
        binding.acPaymentMethod.populate(paymentMethods) {
            viewModel.paymentMethodField.liveData.value = it
            if (it.equals("Efectivo")) {
                binding.tlElectronicPayerName.visibility = View.GONE
            }else{
                binding.tlElectronicPayerName.visibility = View.VISIBLE
            }
        }
    }

}
