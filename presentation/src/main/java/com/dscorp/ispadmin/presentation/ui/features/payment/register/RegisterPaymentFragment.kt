package com.dscorp.ispadmin.presentation.ui.features.payment.register

import android.os.Bundle
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
    }

    override fun handleState(state: RegisterPaymentUiState) =
        when (state) {
            is RegisterPaymentUiState.OnPaymentRegistered -> showSuccessDialog("Pago registrado") {
                findNavController().popBackStack()
            }

            is RegisterPaymentUiState.HideDiscountFields -> {
                    binding.textView.text = getString(R.string.register_payment_is_by_reconnection)
                    binding.tlPaymentDiscount.visibility = android.view.View.GONE
                    binding.tlPaymentDiscountReason.visibility = android.view.View.GONE
            }
        }

    private fun setupView() {
        binding.tvPlan.text = "Deuda: ${args.payment.amountToPayStr()}"
        populatePaymentMethodSpinner()
    }


    private fun populatePaymentMethodSpinner() {
        val paymentMethods =
            resources.getStringArray(com.dscorp.ispadmin.R.array.payment_methods).asList()
        binding.acPaymentMethod.populate(paymentMethods) {
            viewModel.paymentMethodField.liveData.value = it
        }
    }

}
