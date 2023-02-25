package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.databinding.FragmentIpPoolBinding
import com.dscorp.ispadmin.presentation.extension.fill
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.IpPool
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.data2.data.apirequestmodel.IpPoolRequest
import org.koin.android.ext.android.inject

class IpPoolFragment : BaseFragment(), IpPoolSelectionListener {

    private var selectedHostDevice: NetworkDevice? = null
    val binding by lazy { FragmentIpPoolBinding.inflate(layoutInflater) }

    private val viewModel: IpPoolViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observeResponse()
        viewModel.getHostDevices()
        viewModel.getIpPoolList()
        binding.btnRegisterIpSegment.setOnClickListener {
            registerIpSegment()
        }
        return binding.root
    }

    private fun registerIpSegment() {
        val ipPool = IpPoolRequest(
            ipSegment = binding.etIpSegment.text.toString(),
            hostDeviceId = selectedHostDevice?.id
        )
        viewModel.registerIpPool(ipPool)
    }

    private fun observeResponse() {
        viewModel.uiState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is IpPoolUiState.IpPoolRegister -> showSuccessDialog(response)
                is IpPoolUiState.IpPoolRegisterError -> showErrorDialog()
                is IpPoolUiState.IpPoolCleanError -> binding.tlIpSegment.error = null
                is IpPoolUiState.IpPoolCleanInvalidIpSegment -> binding.tlIpSegment.error = null
                is IpPoolUiState.IpPoolError -> binding.tlIpSegment.error = response.error
                is IpPoolUiState.IpPoolInvalidIpSegment ->
                    binding.tlIpSegment.error =
                        response.error
                is IpPoolUiState.IpPoolList -> fillRecycleView(response)
                is IpPoolUiState.IpPoolListError -> {}
                is IpPoolUiState.HostDevicesError -> showErrorDialog(response.message)
                is IpPoolUiState.HostDevicesReady -> fillHostDevicesSpinner(response.hostDevices)
                IpPoolUiState.CleanNoHostDeviceSelectedError -> binding.spnHostDevice.error = null
                IpPoolUiState.NoHostDeviceSelectedError -> showErrorDialog(response.error)
            }
        }
    }

    private fun fillHostDevicesSpinner(hostDevices: List<NetworkDevice>) {
        binding.spnHostDevice.fill(hostDevices) {
            selectedHostDevice = it
        }
    }

    private fun showSuccessDialog(response: IpPoolUiState.IpPoolRegister) {
        showSuccessDialog("La Ip ${response.ipPool.ipSegment} ah sido registrado correctamente")
    }

    private fun fillRecycleView(it: IpPoolUiState.IpPoolList) {
        val adapter = IpPoolAdapter(this)
        binding.rvIpPool.adapter = adapter
        adapter.submitList(it.ipPools)
        binding.rvIpPool.visibility =
            if (it.ipPools.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onIpPoolSelected(ipPool: IpPool) {
        val action = IpPoolFragmentDirections.toIpList(ipPool)
        findNavController().navigate(action)
    }
}
