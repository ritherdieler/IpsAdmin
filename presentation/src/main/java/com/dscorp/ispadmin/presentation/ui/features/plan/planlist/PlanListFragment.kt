package com.dscorp.ispadmin.presentation.ui.features.plan.planlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapBoxesListBinding
import com.dscorp.ispadmin.databinding.FragmentPlanListBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlanListFragment : BaseFragment() {

    private lateinit var binding: FragmentPlanListBinding
    private val viewModel: PlanListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_plan_list,
                null,
                true
            )
        observe()
        return binding.root
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.responseLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is PlanListResponse.OnError -> {}
                    is PlanListResponse.OnPlanListFound -> fillRecycleView(it)
                }
            }
        }
    }

    private fun fillRecycleView(it: PlanListResponse.OnPlanListFound) {
        val adapter = PlanAdapter()
        adapter.submitList(it.planList)
        binding.rvPlanList.adapter = adapter

        binding.rvPlanList.visibility =
            if (it.planList.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
