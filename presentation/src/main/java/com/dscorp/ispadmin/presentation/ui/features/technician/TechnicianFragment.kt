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
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionFormError
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class TechnicianFragment : Fragment(R.layout.fragment_technician) {
    lateinit var binding: FragmentTechnicianBinding
    var selectedDate: Long = 0
    val viewModel: TechnicianViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_technician, null, true)

        observeResponse()
        observeFromError()

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
        val edDni: EditText = binding.etDni

        edDni.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length < 8) {
                        viewModel.technicianFromErrorLiveData.postValue(
                            TechnicianFromError.OnEtDniError(
                                "La cantidad mínima de caracteres para el número de teléfono es de 8"
                            )
                        )
                        return
                    }
                }
            }
        })
        val edPhone: EditText = binding.etPhone

        edPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length < 9) {
                        viewModel.technicianFromErrorLiveData.postValue(
                            TechnicianFromError.OnEtPhoneError(
                                "La cantidad mínima de caracteres para el número de teléfono es de 9"
                            )
                        )
                        return
                    }
                }
            }
        })
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
                is TechnicianResponse.OnError -> showErrorDialog(response.error.message.toString())
                is TechnicianResponse.OnTechnicianRegistered -> showSucessDialog(response.technician.firstName)
            }
        }
    }

    private fun showSucessDialog(technician: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El tecnico fue registrado con exito")
        builder.setMessage(technician)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El Tenico no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()

    }

    private fun observeFromError() {
        viewModel.technicianFromErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is TechnicianFromError.OnEtFirstNameError -> binding.etFirstName.setError(formError.error)
                is TechnicianFromError.OnEtLastNameError -> binding.etLastName.setError(formError.error)
                is TechnicianFromError.OnEtDniError -> binding.etDni.setError(formError.error)
                is TechnicianFromError.OnEtTypeError -> binding.etType.setError(formError.error)
                is TechnicianFromError.OnEtUserNameError -> binding.etUsername.setError(formError.error)
                is TechnicianFromError.OnEtPasswordError -> binding.etPassword.setError(formError.error)
                is TechnicianFromError.OnEtAddressError -> binding.etAddress.setError(formError.error)
                is TechnicianFromError.OnEtPhoneError -> binding.etPhone.setError(formError.error)
                is TechnicianFromError.OnEtBirthdayError -> binding.etBirthday.setError(formError.error)

            }
        }
    }
}






