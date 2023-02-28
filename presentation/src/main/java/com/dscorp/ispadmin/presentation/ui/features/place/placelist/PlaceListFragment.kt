package com.dscorp.ispadmin.presentation.ui.features.place.placelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNetworkDeviceListBinding
import com.dscorp.ispadmin.databinding.FragmentPlaceListBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaceListFragment : BaseFragment() {

    private lateinit var binding: FragmentPlaceListBinding
    private val viewModel: PlaceListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_place_list,
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
                    is PlaceListResponse.OnError -> {}
                    is PlaceListResponse.OnPlaceListFound -> fillRecycleView(it)
                }
            }
        }
    }

    private fun fillRecycleView(it: PlaceListResponse.OnPlaceListFound) {
        val adapter = PlaceAdapter()
        adapter.submitList(it.placeList)
        binding.rvPlaceList.adapter = adapter

        binding.rvPlaceList.visibility =
            if (it.placeList.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
