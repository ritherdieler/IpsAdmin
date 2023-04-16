package com.dscorp.ispadmin.presentation.ui.features.technician

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentTechnicianBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class TechnicianFragment : BaseFragment() {
    lateinit var binding: FragmentTechnicianBinding
    var selectedDate: Long = 0
    val viewModel: TechnicianViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_technician, null, true)

        observeResponse()
        observeFromError()
        observeErroCleanForm()

        binding.btRegisterTechnician.setOnClickListener {
            registerTechnician()
        }
        binding.etBirthday.setOnClickListener {

            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setEnd(today)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            datePicker.addOnPositiveButtonClickListener {
                selectedDate = it
                val formatter = SimpleDateFormat("dd/MM/yyyy")

                val formattedDate = formatter.format(it)
                binding.etBirthday.setText(formattedDate)
            }

            datePicker.show(childFragmentManager, "DatePicker")
        }

        return binding.root
    }

    private fun registerTechnician() {
        val technician = Technician(
            name = binding.etFirstName.text.toString(),
            lastName = binding.etLastName.text.toString(),
            dni = binding.etDni.text.toString(),
            type = binding.etType.text.toString(),
            username = binding.etUsername.text.toString(),
            password = binding.etPassword.text.toString(),
            address = binding.etAddress.text.toString(),
            phone = binding.etPhone.text.toString(),
            birthday = selectedDate

        )

        viewModel.registerTechnician(technician)
    }

    private fun observeResponse() {
        viewModel.technicianResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is TechnicianResponse.OnError -> showErrorDialog()
                is TechnicianResponse.OnTechnicianRegistered -> showSuccessDialog(response)
            }
        }
    }
    private fun showSuccessDialog(response: TechnicianResponse.OnTechnicianRegistered) {
        showSuccessDialog("El técnico${response.technician.name} ah sido registrado exitosamente")
    }

    private fun observeFromError() {
        viewModel.technicianFromErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is TechnicianFromError.OnEtFirstNameError -> binding.tlFirstName.error = formError.error
                is TechnicianFromError.OnEtLastNameError -> binding.tlLastName.error = formError.error
                is TechnicianFromError.OnEtDniError -> binding.tlDni.error = formError.error
                is TechnicianFromError.OnEtTypeError -> binding.tlType.error = formError.error
                is TechnicianFromError.OnEtUserNameError -> binding.tlUserName.error = formError.error
                is TechnicianFromError.OnEtPasswordError -> binding.tlPassword.error = formError.error
                is TechnicianFromError.OnEtAddressError -> binding.tlAddress.error = formError.error
                is TechnicianFromError.OnEtPhoneError -> binding.tlPhone.error = formError.error
                is TechnicianFromError.OnEtBirthdayError -> binding.tlBirthday.error = formError.error
                is TechnicianFromError.OnEtLastNameIsInvalidError -> binding.tlLastName.error = formError.error
                is TechnicianFromError.OnEtFirstNameIsInvalidError -> binding.tlFirstName.error = formError.error
                is TechnicianFromError.OnEtPasswordIsInvalidError -> binding.tlPassword.error = formError.error
            }
        }
    }
    private fun observeErroCleanForm() {
        viewModel.technicianErrorCleanFormLiveData.observe(viewLifecycleOwner) { errorCleanForm ->
            when (errorCleanForm) {
                TechnicianErrorCleanForm.OnEtAddressCleanError -> binding.tlAddress.error = null
                TechnicianErrorCleanForm.OnEtBirthdayCleanError -> binding.tlBirthday.error = null
                TechnicianErrorCleanForm.OnEtDniCleanError -> binding.tlDni.error = null
                TechnicianErrorCleanForm.OnEtFirstNameCleanError -> binding.tlFirstName.error = null
                TechnicianErrorCleanForm.OnEtLastNameCleanError -> binding.tlLastName.error = null
                TechnicianErrorCleanForm.OnEtPasswordCleanError -> binding.tlPassword.error = null
                TechnicianErrorCleanForm.OnEtPhoneCleanError -> binding.tlPhone.error = null
                TechnicianErrorCleanForm.OnEtTypeCleanError -> binding.tlType.error = null
                TechnicianErrorCleanForm.OnEtUserNameCleanError -> binding.tlUserName.error = null
            }
        }
    }
}
