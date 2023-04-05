package com.dscorp.ispadmin.presentation.ui.features.plan.planlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.databinding.FragmentPlanListBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlanListFragment : BaseFragment() {

    private val binding by lazy { FragmentPlanListBinding.inflate(layoutInflater) }
    private val viewModel: PlanListViewModel by viewModel()
    private val planAdapter by lazy { PlanAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.adapter = planAdapter
        binding.executePendingBindings()
        observe()
        return binding.root
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.responseLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is PlanListUiState.OnError -> showErrorDialog(it.error.message)
                    is PlanListUiState.OnPlanListFound -> planAdapter.submitList(it.planList)
                }
            }
        }
    }

}
