package com.dscorp.ispadmin.presentation.ui.features.networkdevice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNetworkDeviceBinding
import com.dscorp.ispadmin.presentation.extension.fillWithList
import com.dscorp.ispadmin.presentation.ui.features.networkdevice.NetworkDeviceFormError.*
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import org.koin.androidx.viewmodel.ext.android.viewModel

class NetworkDeviceFragment : Fragment(R.layout.fragment_network_device) {
    private val binding by lazy { FragmentNetworkDeviceBinding.inflate(layoutInflater) }
    private val viewModel: NetworkDeviceViewModel by viewModel()
    private var selectedNetworkDeviceType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observeNetWorkDeviceResponse()
        observeNetworkDeviceFormError()
        binding.btRegisterNetworkDevice.setOnClickListener {
            registerNetworkDevice()
        }

        return binding.root
    }

    private fun observeNetWorkDeviceResponse() {
        viewModel.networkDeviceResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkDeviceResponse.OnNetworkDeviceRegistered -> showSuccessDialog(response.networkDevice.name)
                is NetworkDeviceResponse.OnError -> showErrorLiveData(response.error.message.toString())
                is NetworkDeviceResponse.OnNetworkDeviceTypesReceived -> fillDeviceTypeSpinner(
                    response.networkDeviceTypes
                )
            }
        }
    }

    private fun fillDeviceTypeSpinner(networkDeviceTypes: List<String>) {
        binding.etDeviceType.fillWithList(networkDeviceTypes) {
            selectedNetworkDeviceType = it as String
        }
    }

    private fun observeNetworkDeviceFormError() {
        viewModel.networkDeviceFormErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtAddressError -> binding.tlIpAddress.error = formError.message
                is OnEtNameError -> binding.tlName.error = formError.message
                is OnEtPasswordError -> binding.tlPassword.error = formError.message
                is OnEtUserNameError -> binding.tlUserName.error = formError.message
                is OnDeviceTypeError -> binding.spnDeviceType.error = formError.message
            }
        }
    }

    private fun showSuccessDialog(networkDeviceName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Dispositivo $networkDeviceName registrado correctamente.")
        builder.setMessage("")
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun showErrorLiveData(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ERROR:el dispositivo de red no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun registerNetworkDevice() {
        val networkDevice = NetworkDevice(
            name = binding.etName.text.toString(),
            password = binding.etPassword.text.toString(),
            username = binding.etUsername.text.toString(),
            ipAddress = binding.etIpAddress.text.toString(),
            networkDeviceType = selectedNetworkDeviceType
        )
        viewModel.registerNetworkDevice(networkDevice)
    }
}
