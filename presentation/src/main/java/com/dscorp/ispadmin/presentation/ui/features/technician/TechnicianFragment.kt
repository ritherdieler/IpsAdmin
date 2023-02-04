package com.dscorp.ispadmin.presentation.ui.features.technician

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentTechnicianBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.util.IDialogFactory
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class TechnicianFragment : Fragment(R.layout.fragment_technician) {
    lateinit var binding: FragmentTechnicianBinding
    var selectedDate: Long = 0
    private val dialogFactory: IDialogFactory by inject()
    val viewModel: TechnicianViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
            firstName = binding.etFirstName.text.toString(),
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
                is TechnicianResponse.OnTechnicianRegistered -> showSuccessDialog(response.technician.firstName)
            }
        }
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
            }
        }
    }
    private fun observeErroCleanForm() {
        viewModel.technicianErrorCleanFormLiveData.observe(viewLifecycleOwner) { errorCleanForm ->
            when(errorCleanForm){
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






