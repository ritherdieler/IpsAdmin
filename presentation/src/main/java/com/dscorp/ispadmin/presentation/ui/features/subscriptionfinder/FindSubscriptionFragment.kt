package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentFindSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.domain.entity.User
import com.example.cleanarchitecture.domain.domain.entity.extensions.localToUTC
import com.example.cleanarchitecture.domain.domain.entity.extensions.toFormattedDateString
import com.google.android.material.datepicker.*
import org.koin.android.ext.android.inject
import java.util.*

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

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        binding.findSubscriptionRecyclerView.adapter = adapter

        binding.etStartDate.setOnClickListener {
            showStartDatePickerDialog()
        }
        binding.etEndDate.setOnClickListener {
            showEndDatePickerDialog()
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
                is FindSubscriptionUiState.OnError -> showErrorDialog(it.message)
            }
        }
    }

    private fun showStartDatePickerDialog() {

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.startDateField.value = it.localToUTC()
            viewModel.startDateField.value?.let { date ->
                binding.etStartDate.setText(date.toFormattedDateString())
            }
        }
        datePicker.show(childFragmentManager, "DatePickerStart")
    }


    private fun showEndDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.endDateField.value = it.localToUTC()
            viewModel.endDateField.value?.let { date ->
                binding.etEndDate.setText(date.toFormattedDateString())
            }
        }
        datePicker.show(childFragmentManager, "DatePickerend")
    }

    override fun onSubscriptionPopupButtonSelected(subscription: SubscriptionResponse, view: View) {
        showPopupMenu(view, subscription)
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
                R.id.btn_see_details -> navigateToDetails(subscription)
                else -> false
            }
        }
        popupMenu.inflate(R.menu.subscription_menu)
        if (viewModel.user!!.type == User.UserType.TECHNICIAN) {
            popupMenu.menu.findItem(R.id.btn_edit_plan_subscription).isVisible = false
            popupMenu.menu.findItem(R.id.btn_register_service_order).isVisible = false
        }
        popupMenu.show()
    }

    private fun navigateToDetails(subscription: SubscriptionResponse): Boolean {
        val destination =
            FindSubscriptionFragmentDirections.findSubscriptionToSubscriptionDetail(subscription)
        findNavController().navigate(destination)
        return true
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
