package com.dscorp.ispadmin.presentation.napbox

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapBoxBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NapBoxFragment : Fragment() {
    lateinit var binding: FragmentNapBoxBinding
    val viewModel: NapBoxViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_nap_box, null, true)
        observeNapBoxResponse()
        observeNapBoxFormError()

        binding.btRegisterNapBox.setOnClickListener {
            registerNapBox()
        }
        return binding.root
    }

    private fun registerNapBox() {
        val code = binding.etCode.text.toString()
        val address = binding.etAddress.text.toString()
        viewModel.registerNapBox(code, address)
    }

    private fun observeNapBoxFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is NapBoxFormError.OnEtAbbreviationError -> binding.etAddress.error =
                    formError.error
                is NapBoxFormError.OnEtNameNapBoxError -> binding.etCode.error = formError.error
            }
        }
    }

    private fun observeNapBoxResponse() {
        viewModel.napBoxResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NapBoxResponse.OnError -> showErrorDialog(response.error.message.toString())
                is NapBoxResponse.OnNapBoxRegister -> showSucessDialog(response.napBox.code)
            }
        }
    }

    private fun showSucessDialog(napBox: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Caja nap registrado con exito")
        builder.setMessage(napBox)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El Caja nap no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }
}