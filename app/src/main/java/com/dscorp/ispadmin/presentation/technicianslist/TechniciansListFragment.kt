package com.dscorp.ispadmin.presentation.technicianslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentTechnicianBinding
import com.dscorp.ispadmin.databinding.FragmentTechniciansListBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TechniciansListFragment : Fragment() {
    private lateinit var binding: FragmentTechniciansListBinding
    private val viewModel: TechniciansListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        val adapter = TechniciansListAdapter()
        adapter.submitList(it.techniciansList)
        binding.rvTechnicianList.adapter = adapter

        binding.rvTechnicianList.visibility = if (it.techniciansList.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
