package com.dscorp.ispadmin.presentation.ui.features.payment.history

import android.os.Bundle
import android.os.Parcel
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentConsultPaymentsBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryFragmentDirections.actionPaymentHistoryFragmentToPaymentDetailFragment
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryFragmentDirections.toRegisterPayment
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.GetRecentPaymentsHistoryError
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.GetRecentPaymentsHistoryResponse
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.OnError
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.OnPaymentHistoryFilteredResponse
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.ServiceReactivated
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class PaymentHistoryFragment :
    BaseFragment<PaymentHistoryUiState, FragmentConsultPaymentsBinding>(),
    PaymentHistoryAdapterListener {

    override val viewModel: PaymentHistoryViewModel by viewModel()
    override val binding by lazy { FragmentConsultPaymentsBinding.inflate(layoutInflater) }
    private val args: PaymentHistoryFragmentArgs by navArgs()
    val adapter by lazy { PaymentHistoryAdapter(this) }
    private var selectedEndDate: Long? = null
    private var selectedStartDate: Long? = null

    override fun handleState(state: PaymentHistoryUiState) {
        when (state) {
            is OnError -> showErrorDialog(state.message)
            is OnPaymentHistoryFilteredResponse -> {
                fillPaymentHistoryFiltered(state.payments)
                binding.tvDisclaimer.visibility = View.GONE
            }

            is GetRecentPaymentsHistoryResponse -> {
                fillRecentPaymentHistory(state.payments)
            }

            is GetRecentPaymentsHistoryError -> showErrorDialog(state.message)
            is ServiceReactivated -> showSuccessDialog(getString(R.string.service_reactivated_successfully))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscription = args.subscription
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupViews()
        setupListeners()
        binding.rvPayments.adapter = adapter
        getPayments()
    }

    private fun setupViews() {
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }

    private fun setupListeners() {
        binding.etStartDate.setOnClickListener {
            showDatePickerDialog {
                selectedStartDate = it
            }
        }
        binding.etEndDate.setOnClickListener {
            showDatePickerDialog {
                selectedEndDate = it
            }
        }
        binding.btnConsult.setOnClickListener {
            findFilteredPayments()
        }
        binding.cbOnlyPending.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.showOnlyPendingPayments()
            else viewModel.showAllPayments()
        }
    }

    private fun getPayments() {
        viewModel.getLastPayments(
            args.subscription.id,
            PaymentHistoryViewModel.LAST_PAYMENTS_ROW_LIMIT
        )
    }

    private fun fillPaymentHistoryFiltered(payments: List<Payment>) {
        binding.tvDisclaimer.visibility = View.GONE
        adapter.submitList(payments)
    }

    private fun fillRecentPaymentHistory(payments: List<Payment>) {
        adapter.submitList(payments)
    }

    private fun showDatePickerDialog(callback: (Long) -> Unit = {}) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val datePicker = createDatePicker(calendar, callback)
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun createDatePicker(
        initialCalendar: Calendar,
        callback: (Long) -> Unit
    ): MaterialDatePicker<Long> {
        val dateValidator = getDateValidator()
        return MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(initialCalendar.timeInMillis)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(dateValidator)
                    .build()
            )
            .build()
            .apply {
                addOnPositiveButtonClickListener { callback(it) }
            }
    }

    private fun getDateValidator(): CalendarConstraints.DateValidator {
        return object : CalendarConstraints.DateValidator {
            override fun isValid(date: Long): Boolean {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                return calendar.get(Calendar.DAY_OF_MONTH) == 1
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {}
            override fun describeContents(): Int = 0
        }

    }

    private fun findFilteredPayments() {
        binding.cbOnlyPending.isChecked = false
        val mStartDate = selectedStartDate ?: return
        val mEndDate = selectedEndDate ?: return
        viewModel.getFilteredPaymentHistory(
            SearchPaymentsRequest().apply {
                startDate = mStartDate
                endDate = mEndDate
                subscriptionId = args.subscription.id
            }
        )
    }

    override fun onPaymentHistoryItemClicked(payment: Payment) {
        val action = if (!payment.paid) toRegisterPayment(payment)
        else actionPaymentHistoryFragmentToPaymentDetailFragment(payment)
        findNavController().navigate(action)
    }

}
