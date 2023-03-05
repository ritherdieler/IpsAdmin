package com.dscorp.ispadmin.presentation.ui.features.ippool.seeip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    val sortedList = ips.sortedByDescending { it.ip.split(".")[3] }
    LazyColumn(Modifier.padding(horizontal = 24.dp)) {
        items(sortedList.size) { index ->
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                Modifier
                    .height(30.dp)
                    .border(0.5.dp, Color.Black),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = sortedList[index].ip,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

        }
    }
}

@Preview
@Composable
fun composableIpListPreview() {
    composableIpList(
        listOf(
            Ip(id = 1, ip = "192.168.0.1"),
            Ip(id = 2, ip = "192.168.0.2"),
            Ip(id = 3, ip = "192.168.0.3"),
            Ip(id = 4, ip = "192.168.0.4"),
            Ip(id = 5, ip = "192.168.0.5"),
        )
    )
}