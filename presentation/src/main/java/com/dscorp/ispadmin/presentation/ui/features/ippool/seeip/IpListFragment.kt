package com.dscorp.ispadmin.presentation.ui.features.ippool.seeip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
        binding.composeView.setContent {
            composableIpList(ips)
        }


    }

    private fun getIpList() {
        args.ipPool.id?.let { viewModel.getIpList(it) }

    }


}

@Composable
fun composableIpList(ips: List<Ip>) {


    LazyColumn {

        items(ips.size) { index ->
            Text(
                text = ips[index].ip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(Color.Blue),
                textAlign = TextAlign.Center,

            )
        }
    }


}