package com.dscorp.ispadmin.presentation.ui.features.payment.history

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentConsultPaymentsBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryUiState.*
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.google.android.material.datepicker.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class PaymentHistoryFragment : BaseFragment(), View.OnClickListener, PaymentHistoryAdapterListener {
    private val args: PaymentHistoryFragmentArgs by navArgs()
    val binding by lazy { FragmentConsultPaymentsBinding.inflate(layoutInflater) }
    private val viewModel: PaymentHistoryViewModel by inject()
    val adapter by lazy { PaymentHistoryAdapter(this) }

    private var selectedEndDate: Long? = null
    private var selectedStartDate: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initClickListeners()
        binding.rvPayments.adapter = adapter
        getPayments()
        binding.cbOnlyPending.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.showOnlyPendingPayments()
            else viewModel.showAllPayments()
        }

        observeUiState()

        return binding.root
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.etStartDate -> showStartDatePickerDialog { selectedStartDate = it }
            binding.etEndDate -> showEndDatePickerDialog { selectedEndDate = it }
            binding.btnConsult -> findFilteredPayments()
        }
    }

    private fun observeUiState() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is OnError -> showErrorDialog(it.message)
                is OnPaymentHistoryFilteredResponse -> fillPaymentHistoryFiltered(it.payments)
                is GetRecentPaymentsHistoryResponse -> fillRecentPaymentHistory(it.payments)
                is GetRecentPaymentHistoryError -> showErrorDialog(it.message)
            }
        }
    }

    private fun getPayments() {
        viewModel.getLastPayments(
            args.subscription.id!!,
            PaymentHistoryViewModel.LAST_PAYMENTS_LIMIT
        )
    }

    private fun initClickListeners() {
        binding.etStartDate.setOnClickListener(this)
        binding.etEndDate.setOnClickListener(this)
        binding.btnConsult.setOnClickListener(this)
    }

    private fun fillPaymentHistoryFiltered(payments: List<Payment>) {
        binding.tvDisclaimer.visibility = View.GONE
        adapter.submitList(payments)
    }

    private fun fillRecentPaymentHistory(payments: List<Payment>) {
        adapter.submitList(payments)
    }

    private fun showStartDatePickerDialog(callback: (Long) -> Unit = {}) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dateValidator = object : CalendarConstraints.DateValidator {
            override fun isValid(date: Long): Boolean {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                return calendar.get(Calendar.DAY_OF_MONTH) == 1
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {}
            override fun describeContents(): Int = 0
        }
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(calendar.timeInMillis)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(dateValidator)
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = formatter.format(it)
            binding.etStartDate.setText(formattedDate)
            callback(it)
        }
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun showEndDatePickerDialog(callback: (Long) -> Unit = {}) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dateValidator = object : CalendarConstraints.DateValidator {
            override fun isValid(date: Long): Boolean {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                return calendar.get(Calendar.DAY_OF_MONTH) == 1
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {}
            override fun describeContents(): Int = 0
        }
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(calendar.timeInMillis)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(dateValidator)
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = formatter.format(it)
            binding.etEndDate.setText(formattedDate)
            callback(it)
        }
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun findFilteredPayments() {
        binding.cbOnlyPending.isChecked = false
        viewModel.getFilteredPaymentHistory(SearchPaymentsRequest().apply {
            startDate = selectedStartDate
            endDate = selectedEndDate
            subscriptionId = args.subscription.id
        })
    }

    override fun onPaymentHistoryItemClicked(payment: Payment) {
        val action =
            PaymentHistoryFragmentDirections.actionPaymentHistoryFragmentToPaymentDetailFragment(
                payment
            )
        findNavController().navigate(action)
    }

}