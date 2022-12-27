package com.dscorp.ispadmin.presentation.technician

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentTechnicianBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TechnicianFragment : Fragment() {
    lateinit var binding: FragmentTechnicianBinding
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
        return binding.root
    }
    private fun registerTechnician() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val dni = binding.etDni.text.toString()
        val type = binding.etType.text.toString()
        val userName = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        val address = binding.etAddress.text.toString()
        val phone = binding.etPhone.text.toString()
        val birthday = binding.etBirthday.text.toString()
        viewModel.registerTechnician(firstName,lastName,dni,type,userName,password,address,phone,birthday)
    }

    private fun observeResponse() {
        viewModel.technicianResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is TechnicianResponse.OnError -> showErrorDialog(response.error.message.toString())
                is TechnicianResponse.OnTechnicianRegistered ->showSucessDialog(response.technician.name)
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

    private fun showErrorDialog(error: String) {  val builder = AlertDialog.Builder(requireContext())
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






