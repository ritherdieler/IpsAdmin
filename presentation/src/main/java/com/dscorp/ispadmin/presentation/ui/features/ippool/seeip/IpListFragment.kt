package com.dscorp.ispadmin.presentation.ui.features.ippool.seeip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentIpListBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.Ip
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class IpListFragment : BaseFragment() {

    private val args by navArgs<IpListFragmentArgs>()

    val binding by lazy { FragmentIpListBinding.inflate(layoutInflater) }

    val viewModel: IpListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getIpList()
        observeUiState()
        return binding.root
    }

    private fun observeUiState() = lifecycleScope.launch {
        viewModel.uiState.collect {
            when (it) {
                IpListUiState.Idle -> {}
                is IpListUiState.IpListError -> showErrorDialog(it.error)
                is IpListUiState.IpListReady -> fillIpRecyclerView(it.ips)
            }
        }
    }

    private fun fillIpRecyclerView(ips: List<Ip>) {

        binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Text("Hello Compose!")
                }
            }
        }

    }

    private fun getIpList() {
        args.ipPool.id?.let { viewModel.getIpList(it) }

    }


}