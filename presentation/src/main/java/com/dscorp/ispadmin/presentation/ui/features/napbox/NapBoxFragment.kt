package com.dscorp.ispadmin.presentation.ui.features.napbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapBoxBinding
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

class NapBoxFragment : Fragment() {
    var selectedLocation: LatLng? = null
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
            findNavController().navigateSafe(R.id.action_nav_to_register_nap_box_to_mapDialog)

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

    @SuppressLint("SetTextI18n")
    private fun onLocationSelected(it: LatLng) {
        this.selectedLocation = it
        binding.etLocationNapBox.setText("${it.latitude}, ${it.longitude}")
    }

    private fun registerNapBox() {
        val registerNapBox = NapBox(
            code = binding.etCode.text.toString(),
            address = binding.etAddress.text.toString(),
            location = GeoLocation(selectedLocation!!.latitude, selectedLocation!!.longitude)
        )

        viewModel.registerNapBox(registerNapBox)
    }

    private fun observeNapBoxFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is NapBoxFormError.OnEtAbbreviationError -> binding.etAddress.error =
                    formError.error
                is NapBoxFormError.OnEtNameNapBoxError -> binding.etCode.error = formError.error
                is NapBoxFormError.OnEtLocationError -> binding.etLocationNapBox.error =
                    formError.error
            }
        }
    }

    private fun observeNapBoxResponse() {
        viewModel.napBoxResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NapBoxResponse.OnError -> showErrorDialog()
                is NapBoxResponse.OnNapBoxRegister -> showSuccessDialog(response.napBox.code)

            }
        }
    }
}