package com.dscorp.ispadmin.presentation.ui.features.ipPool.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dscorp.ispadmin.databinding.FragmentIpPoolBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.example.cleanarchitecture.domain.domain.entity.IpPool
import org.koin.android.ext.android.inject

class IpPoolFragment : Fragment() {

    val binding by lazy { FragmentIpPoolBinding.inflate(layoutInflater) }

    private val viewModel: IpPoolViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observeResponse()

        binding.btnRegisterIpSegment.setOnClickListener {
            registerIpSegment()
        }
        return binding.root
    }


    private fun registerIpSegment() {
        val ipPool = IpPool(
            ipSegment = binding.etFirstName.text.toString()
        )
        viewModel.registerIpPool(ipPool)
    }


    private fun observeResponse() {
        viewModel.uiState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is IpPoolUiState.IpPoolCreated -> showSuccessDialog(response)
                is IpPoolUiState.IpPoolRegisterError -> showErrorDialog()
                IpPoolUiState.IpPoolCleanError -> binding.tlIpSegment.error = null
                IpPoolUiState.IpPoolCleanInvalidIpSegment -> binding.tlIpSegment.error = null
                is IpPoolUiState.IpPoolError -> binding.tlIpSegment.error = response.error
                is IpPoolUiState.IpPoolInvalidIpSegment -> binding.tlIpSegment.error =
                    response.error
            }
        }
    }

    private fun showSuccessDialog(response: IpPoolUiState.IpPoolCreated) {
        showSuccessDialog("La Ip ${response.ipPool.ipSegment} ah sido registrado correctamente")
    }
}
