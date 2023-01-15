package com.dscorp.ispadmin.presentation.place

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentPlaceBinding
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaceFragment() : Fragment() {
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onLocationPermissionResult
    )
    lateinit var binding: FragmentPlaceBinding
    val viewModel: PlaceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_place, null, true)
        observePlaceResponse()
        observeFormError()

        binding.btRegisterPlace.setOnClickListener {
            registerPlace()

        }
        binding.etLocation.setOnClickListener {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        observeMapDialogResult()
        return binding.root
    }

    private fun observeMapDialogResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("location")
            ?.observe(viewLifecycleOwner) {
                onLocationSelected(it)
            }
    }

    private fun onLocationSelected(it: LatLng) {
        toast("Latitud: ${it.latitude} Longitud: ${it.longitude}")

    }

    private fun registerPlace() {
        val namePlace = binding.etNamePlace.text.toString()
        val abbreviation = binding.etAbbreviation.text.toString()
        viewModel.registerPlace(namePlace, abbreviation)
    }

    private fun observeFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is FormError.OnEtAbbreviationError -> binding.etAbbreviation.error = formError.error
                is FormError.OnEtNamePlaceError -> binding.etNamePlace.error = formError.error
            }
        }
    }

    private fun observePlaceResponse() {
        viewModel.placeResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.OnError -> showErrorDialog(response.error.message.toString())
                is Response.OnPlaceRegister -> showSuccessDialog(response.place.name)
            }
        }
    }

    private fun showSuccessDialog(place: String) {
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

    private fun onLocationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            findNavController().navigateSafe(R.id.action_nav_register_place_to_mapDialog)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se puede acceder a la ubicación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showRationaleDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Esta seccion necesita el permiso de ubicacion para funcionar correctamente.")
            .setPositiveButton("OK") { _, _ ->
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle user canceling the dialog
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun toast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

}