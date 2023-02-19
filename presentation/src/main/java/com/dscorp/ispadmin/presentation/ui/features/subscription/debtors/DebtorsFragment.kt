package com.dscorp.ispadmin.presentation.ui.features.subscription.debtors

import DebtorsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.databinding.FragmentDebtorsBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DebtorsFragment : BaseFragment() {

    private val binding: FragmentDebtorsBinding by lazy { FragmentDebtorsBinding.inflate(layoutInflater) }
    private val viewModel: DebtorsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observe()
        return binding.root
    }
    private fun observe() {
        lifecycleScope.launch {
            viewModel.uiState.observe(viewLifecycleOwner) {
                when (it) {
                    is DebtorsUiState.GetDebtorsError -> {}
                    is DebtorsUiState.GetDebtorsSuccess -> fillRecycleView(it)
                }
            }
        }
    }

    private fun fillRecycleView(it: DebtorsUiState.GetDebtorsSuccess) {
        val adapter = DebtorsAdapter(this)
        adapter.submitList(it.debtors)
        binding.rvDebtors.adapter = adapter
        binding.rvDebtors.visibility =
            if (it.debtors.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
