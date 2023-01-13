package com.dscorp.ispadmin.presentation.napboxeslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapBoxesListBinding
import com.dscorp.ispadmin.domain.entity.NapBox
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NapBoxesListFragment : Fragment() , OnItemClickListener {

        private lateinit var binding: FragmentNapBoxesListBinding
        private val viewModel: NapBoxesListViewModel by viewModel()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View {
            binding =
                DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.fragment_nap_boxes_list,
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
                        is NapBoxesListResponse.OnError -> {}
                        is NapBoxesListResponse.OnNapBoxesListFound -> fillRecycleView(it)
                    }
                }
            }
        }

        private fun fillRecycleView(it: NapBoxesListResponse.OnNapBoxesListFound) {
            val adapter = NapBoxesAdapter(this)
            adapter.submitList(it.napBoxesList)
            binding.rvNapBoxesList.adapter = adapter

            binding.rvNapBoxesList.visibility =
                if (it.napBoxesList.isNotEmpty()) View.VISIBLE else View.GONE
        }

    override fun onItemClick(napBox: NapBox) {
        parentFragmentManager.beginTransaction().apply {
            val destination =
                NapBoxesListFragmentDirections.actionNavSeeNapBoxesToNapBoxeDetailsFragment(napBox
                )
            findNavController().navigate(destination)
        }

        }
    }


