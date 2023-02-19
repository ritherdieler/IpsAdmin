package com.dscorp.ispadmin.presentation.ui.features.technicianslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentTechniciansListBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TechniciansListFragment : BaseFragment() {
    private lateinit var binding: FragmentTechniciansListBinding
    private val viewModel: TechniciansListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_technicians_list, null, true)
        observe()
        return binding.root
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.responseLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is TechniciansListResponse.OnError -> {}
                    is TechniciansListResponse.OnTechniciansListFound -> fillRecycleView(it)
                }
            }
        }
    }

    private fun fillRecycleView(it: TechniciansListResponse.OnTechniciansListFound) {
        val adapter = TechniciansListAdapter(this)
        adapter.submitList(it.techniciansList)
        binding.rvTechnicianList.adapter = adapter

        binding.rvTechnicianList.visibility = if (it.techniciansList.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
