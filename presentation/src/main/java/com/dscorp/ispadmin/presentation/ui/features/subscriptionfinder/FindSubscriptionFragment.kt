package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentFindSubscriptionBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject

class FindSubscriptionFragment : BaseFragment(), SelectableSubscriptionListener {

    private val viewModel: FindSubscriptionViewModel by inject()

    private val binding: FragmentFindSubscriptionBinding by lazy {
        FragmentFindSubscriptionBinding.inflate(layoutInflater)
    }

    private val adapter = FindSubscriptionAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.findSubscriptionRecyclerView.adapter = adapter
        binding.btnFind.setOnClickListener {
            val dni = binding.findSubscriptionEditText.text.toString()
            viewModel.findSubscription(dni)
        }

        viewModel.loadingUiState.observe(viewLifecycleOwner) {
            binding.pbarFindSubscription.visibility = if (it) View.VISIBLE else View.GONE
        }

        observeUiState()

        return binding.root
    }

    private fun observeUiState() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is FindSubscriptionUiState.OnSubscriptionFound -> adapter.submitList(it.subscriptions)
                is FindSubscriptionUiState.OnError -> showMaterialDialog(it.message)
            }
        }
    }

    private fun showMaterialDialog(message: String?) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onSubscriptionPopupButtonSelected(subscription: SubscriptionResponse, view: View) {
        showPopupMenu(view, subscription)
    }

    override fun onCardSelected(subscription: SubscriptionResponse) {
        val destination = FindSubscriptionFragmentDirections.findSubscriptionToSubscriptionDetail(subscription)
        findNavController().navigate(destination)
    }

    private fun navigateToEditSubscription(subscription: SubscriptionResponse): Boolean {
        findNavController().navigate(
            FindSubscriptionFragmentDirections.actionNavFindSubscriptionsToEditSubscriptionFragment(
                subscription
            )
        )
        return true
    }

    private fun showPopupMenu(view: View, subscription: SubscriptionResponse) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_show_payment_history -> navigateToPaymentHistory(subscription)
                R.id.btn_register_service_order -> navigateToRegisterServiceOrder(subscription)
                R.id.btn_edit_plan_subscription -> navigateToEditSubscription(subscription)

//                R.id.btn_edit_subscription -> navigateToEditSubscription(subscription)

                else -> false
            }
        }
        popupMenu.inflate(R.menu.subscription_menu)
        popupMenu.show()
    }

    private fun navigateToRegisterServiceOrder(subscription: SubscriptionResponse): Boolean {
        findNavController().navigate(
            FindSubscriptionFragmentDirections.findSubscriptionToRegisterServiceOrder(
                subscription
            )
        )
        return true
    }

    private fun navigateToPaymentHistory(subscription: SubscriptionResponse): Boolean {
        findNavController().navigate(
            FindSubscriptionFragmentDirections.findSubscriptionToPaymentHistoryFragment(
                subscription
            )
        )
        return true
    }
}
