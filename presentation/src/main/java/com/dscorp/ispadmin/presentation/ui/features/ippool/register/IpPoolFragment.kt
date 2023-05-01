package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.databinding.FragmentIpPoolBinding
import com.dscorp.ispadmin.presentation.extension.populate
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.IpPool
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.data2.data.apirequestmodel.IpPoolRequest
import org.koin.androidx.viewmodel.ext.android.viewModel

class IpPoolFragment : BaseFragment<IpPoolUiState, FragmentIpPoolBinding>(),
    IpPoolSelectionListener {

    private var selectedHostDevice: NetworkDevice? = null


    override val viewModel: IpPoolViewModel by viewModel()
    override val binding by lazy { FragmentIpPoolBinding.inflate(layoutInflater) }
    override fun handleState(state: IpPoolUiState) = when (state) {
        is IpPoolUiState.IpPoolRegister -> showSuccessDialog(state)
        is IpPoolUiState.IpPoolCleanError -> binding.tlIpSegment.error = null
        is IpPoolUiState.IpPoolCleanInvalidIpSegment -> binding.tlIpSegment.error = null
        is IpPoolUiState.IpPoolError -> binding.tlIpSegment.error = state.error
        is IpPoolUiState.IpPoolInvalidIpSegment ->
            binding.tlIpSegment.error =
                state.error

        is IpPoolUiState.IpPoolList -> fillRecycleView(state)
        is IpPoolUiState.IpPoolListError -> {}
        is IpPoolUiState.HostDevicesError -> showErrorDialog(state.message)
        is IpPoolUiState.HostDevicesReady -> fillHostDevicesSpinner(state.hostDevices)
        IpPoolUiState.CleanNoHostDeviceSelectedError -> binding.spnHostDevice.error = null
        IpPoolUiState.NoHostDeviceSelectedError -> showErrorDialog(state.error)
        is IpPoolUiState.LoadingData -> {
            binding.viewShimmerIpPool.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.viewContainterIpPoool.visibility =
                if (state.loading) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private fun fillHostDevicesSpinner(hostDevices: List<NetworkDevice>) {
        binding.spnHostDevice.populate(hostDevices) {
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
