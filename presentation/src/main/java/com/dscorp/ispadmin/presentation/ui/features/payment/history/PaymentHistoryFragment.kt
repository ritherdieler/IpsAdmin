package com.dscorp.ispadmin.presentation.ui.features.payment.history

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.databinding.FragmentConsultPaymentsBinding
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.google.android.material.datepicker.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

class PaymentHistoryFragment : Fragment(), View.OnClickListener {

    private val args: PaymentHistoryFragmentArgs by navArgs()
    private var selectedEndDate: Long? = null
    private var selectedStartDate: Long? = null
    private var selectedDate: Long? = null

    private val viewModel: PaymentHistoryViewModel by inject()
    val binding by lazy { FragmentConsultPaymentsBinding.inflate(layoutInflater) }
    val adapter by lazy { PaymentHistoryAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initClickListeners()
        binding.rvPayments.adapter = adapter

        viewModel.resultLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is PaymentHistoryUiState.OnError -> {}
                is PaymentHistoryUiState.OnPaymentResponseHistory -> fillPaymentHistory(it.payments)
            }
        }

        return binding.root
    }

    private fun initClickListeners() {
        binding.etStartDate.setOnClickListener(this)
        binding.etEndDate.setOnClickListener(this)
        binding.btnConsult.setOnClickListener(this)
    }

    private fun fillPaymentHistory(payments: List<Payment>) = adapter.submitList(payments)


   /* private fun showStartDatePickerDialog(callback: (Long) -> Unit = {}) {
        MaterialDatePicker.Builder.datePicker().build().apply {
            addOnPositiveButtonClickListener {
                val selectedTimestamp = it
                val selectedDate = Date(selectedTimestamp)
                val formattedDate = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(selectedDate)
                binding.etStartDate.setText(formattedDate)
                callback(it)
            }
        }.show(childFragmentManager, "DATE_PICKER_START")
    }*/


    private fun showStartDatePickerDialog() {

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
            selectedDate = it
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = formatter.format(it)
            binding.etStartDate.setText(formattedDate)
        }

        datePicker.show(childFragmentManager, "DatePicker")


    }


    private fun showEndDatePickerDialog(callback: (Long) -> Unit = {}) {
        MaterialDatePicker.Builder.datePicker().build().apply {
            addOnPositiveButtonClickListener {
                binding.etEndDate.setText(it.toString())
                callback(it)
            }
        }.show(childFragmentManager, "DATE_PICKER_END")
    }

    override fun onClick(view: View?) {

        when (view) {
            binding.etStartDate -> showStartDatePickerDialog()
            binding.etEndDate -> showEndDatePickerDialog { selectedEndDate = it }
            binding.btnConsult -> {
                viewModel.getPaymentHistory(SearchPaymentsRequest().apply {
                    startDate = selectedStartDate
                    endDate = selectedEndDate
                    subscriptionId = args.subscription.id!!
                })
            }
        }

    }

}