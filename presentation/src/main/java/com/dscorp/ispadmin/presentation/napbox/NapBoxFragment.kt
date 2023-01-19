package com.dscorp.ispadmin.presentation.napbox

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
import com.dscorp.ispadmin.databinding.FragmentNapBoxBinding
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class NapBoxFragment : Fragment() {
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onLocationPermissionResult)

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

        binding.etLocationNapBox.setOnClickListener {
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

    private fun toast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
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

    private fun onLocationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            findNavController().navigateSafe(R.id.action_nav_to_register_nap_box_to_mapDialog)
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
}