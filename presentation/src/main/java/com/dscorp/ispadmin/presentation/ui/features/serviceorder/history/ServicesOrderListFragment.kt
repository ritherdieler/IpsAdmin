package com.dscorp.ispadmin.presentation.ui.features.serviceorder.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentServicesOrderListBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.napboxeslist.NapBoxesListFragmentDirections
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServicesOrderListFragment : Fragment(), OnItemServiceOrderClickListener {
    private lateinit var binding: FragmentServicesOrderListBinding
    private val viewModel: ServicesOrderListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_services_order_list,
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
                    is ServicesOrderListResponse.OnError -> {}
                    is ServicesOrderListResponse.OnServicesOrderListFound -> fillRecycleView(it.servicesOrderList)
                }
            }
        }
    }

    private fun fillRecycleView(servicesOrderList: List<ServiceOrderResponse>) {
        val adapter = ServiceOrderAdapter(this)
        adapter.submitList(servicesOrderList)
        binding.rvServicesOrder.adapter = adapter
        binding.rvServicesOrder.visibility =
            if (servicesOrderList.isNotEmpty()) View.VISIBLE else View.GONE
    }


    private fun showPopupMenu(view: View, serviceOrder: ServiceOrderResponse) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_edit_nap_box -> navigateToEditSubscription(serviceOrder)

                else -> false
            }
        }
        popupMenu.inflate(R.menu.nap_box_menu)
        popupMenu.show()
    }

    override fun onServiceOrderPopupButtonSelected(
        serviceOrderResponse: ServiceOrderResponse,
        view: View
    ) {
        showPopupMenu(view, serviceOrderResponse)
    }

    private fun navigateToEditSubscription(serviceOrder: ServiceOrderResponse): Boolean {
        findNavController().navigate(
            ServicesOrderListFragmentDirections.actionNavViewServicesOrderListToEditServiceOrderFragment(
                serviceOrder
            )
        )
        return true
    }
}
