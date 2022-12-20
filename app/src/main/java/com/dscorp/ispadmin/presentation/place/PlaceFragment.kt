package com.dscorp.ispadmin.presentation.place

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentPlaceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceFragment : Fragment() {
    lateinit var binding: FragmentPlaceBinding
    val viewModel: PlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_place, null, true)
        observePlaceResponse()
        observeFormError()

        binding.btRegisterPlace.setOnClickListener {
            registerPlace()
        }
        return binding.root
    }

    private fun registerPlace() {
        val namePlace = binding.etNamePlace.text.toString()
        val abbreviation = binding.etAbbreviation.text.toString()
        viewModel.registerPlace( namePlace,  abbreviation)
    }

    private fun observeFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is FormError.OnEtAbbreviationError -> binding.etAbbreviation.setError(formError.error)
                is FormError.OnEtNamePlaceError -> binding.etNamePlace.setError(formError.error)
            }
        }
    }

    private fun observePlaceResponse() {
        viewModel.placeResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.OnError -> showErrorDialog(response.error.message.toString())
                is Response.OnPlaceRegister -> showSucessDialog(response.place.name)
            }
        }
    }

    private fun showSucessDialog(place: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Lugar registrado con exito")
        builder.setMessage(place)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El lugar no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }
}