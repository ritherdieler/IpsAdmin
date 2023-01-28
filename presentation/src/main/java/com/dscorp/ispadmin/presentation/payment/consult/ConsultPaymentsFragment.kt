package com.dscorp.ispadmin.presentation.payment.consult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.databinding.FragmentConsultPaymentsBinding
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.google.android.material.datepicker.MaterialDatePicker

class ConsultPaymentsFragment : Fragment(), View.OnClickListener {

    private var selectedEndDate: Long? = null
    private var selectedStartDate: Long? = null

    private lateinit var viewModel: ConsultPaymentsViewModel
    val binding: FragmentConsultPaymentsBinding by lazy {
        FragmentConsultPaymentsBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.etStartDate.setOnClickListener(this)
        binding.etEndDate.setOnClickListener(this)
        binding.btnConsult.setOnClickListener(this)
        return binding.root
    }

    private fun showStartDatePickerDialog(callback: (Long) -> Unit = {}) {
        MaterialDatePicker.Builder.datePicker().build().apply {
            addOnPositiveButtonClickListener {
                binding.etStartDate.setText(it.toString())
                callback(it)
            }
        }.show(childFragmentManager, "DATE_PICKER_START")
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
            binding.etStartDate -> showStartDatePickerDialog { selectedStartDate = it }
            binding.etEndDate -> showEndDatePickerDialog { selectedEndDate = it }
            binding.btnConsult -> {
                viewModel.consultPayments(SearchPaymentsRequest().apply {
                    startDate = selectedStartDate
                    endDate = selectedEndDate
                    subscriptionCode = binding.etSubscriptionCode.text.toString().toInt()
                })
            }
        }

    }

}