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
import com.example.cleanarchitecture.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

@Deprecated("Use PaymentHistoryComposeFragment instead")
class PaymentHistoryFragmentOld :
    BaseFragment<PaymentHistoryUiState, FragmentConsultPaymentsBinding>(),
    PaymentHistoryAdapterListener {

    override val viewModel: PaymentHistoryViewModel by viewModel()
    override val binding by lazy { FragmentConsultPaymentsBinding.inflate(layoutInflater) }
    private val args by navArgs<PaymentHistoryComposeFragmentArgs>()
    val adapter by lazy { PaymentHistoryAdapter(this) }
    private var selectedEndDate: Long? = null
    private var selectedStartDate: Long? = null

    override fun handleState(state: PaymentHistoryUiState) {
        when (state) {
            is PaymentHistoryUiState.OnError -> showErrorDialog(state.message)
            is PaymentHistoryUiState.OnPaymentHistoryFilteredResponse -> {
                fillPaymentHistoryFiltered(state.payments)
                binding.tvDisclaimer.visibility = View.GONE
            }

            is PaymentHistoryUiState.GetRecentPaymentsHistoryResponse -> {
                fillRecentPaymentHistory(state.payments)
            }

            is PaymentHistoryUiState.GetRecentPaymentsHistoryError -> showErrorDialog(state.message)
            is PaymentHistoryUiState.ServiceReactivated -> showSuccessDialog(getString(R.string.service_reactivated_successfully))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscriptionId = args.subscriptionId
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
                subscriptionId = args.subscriptionId
            }
        )
    }

    override fun onPaymentHistoryItemClicked(payment: Payment) {
        // This fragment is deprecated, so we won't navigate
        showErrorDialog("This fragment is deprecated. Please use PaymentHistoryComposeFragment instead.")
    }

}
