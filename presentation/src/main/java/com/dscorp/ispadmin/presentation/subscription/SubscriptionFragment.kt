package com.dscorp.ispadmin.presentation.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.dscorp.ispadmin.presentation.subscription.SubscriptionFormError.*
import com.dscorp.ispadmin.presentation.subscription.SubscriptionResponse.*
import com.example.cleanarchitecture.domain.domain.entity.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class SubscriptionFragment : Fragment() {
    lateinit var binding: FragmentSubscriptionBinding
    var selectedDate: Long = 0
    private var selectedLocation: LatLng? = null
    var selectedPlan: Plan? = null
    var selectedNetworkDevice: NetworkDevice? = null
    var selectedPlace: Place? = null
    private var selectedTechnician: Technician? = null
    val viewModel: SubscriptionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_subscription, null, true)

        observeResponse()
        observeFormError()

        binding.btSubscribirse.setOnClickListener {
            registerSubscription()
        }
        binding.etSubscriptionDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

            datePicker.addOnPositiveButtonClickListener {
                selectedDate = it
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val formattedDate = formatter.format(it)
                binding.etSubscriptionDate.setText(formattedDate)
            }

            datePicker.show(childFragmentManager, "DatePicker")
        }
        binding.etLocationSubscription.setOnClickListener {
            findNavController().navigateSafe(R.id.action_nav_subscription_to_mapDialog)

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
        binding.etLocationSubscription.setText("${it.latitude}, ${it.longitude}")
    }

    private fun observeResponse() {
        viewModel.responseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is OnError -> showErrorDialog(response.error.message.toString())
                is OnFormDataFound -> fillFormSpinners(response)
                is OnSubscriptionRegistered -> showSucessDialog(response.subscription.firstName)
            }
        }
    }

    private fun fillFormSpinners(response: OnFormDataFound) {
        setUpPlansSpinner(response.plans)
        setUpNetworkDeviceSpinner(response.networkDevice)
        setUpPlaceSpinner(response.places)
        setUpTechnicianSpinner(response.technicians)
    }


    private fun observeFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtAddressesError -> setEtAddressError(formError)
                OnFirstNameHasNotErrors -> clearTlFirstNameErrors()
                is OnEtDniError -> setEtDniError(formError)
                is OnEtFirstNameError -> setEtFirstNameError(formError)
                is OnEtLastNameError -> setEtLastNameError(formError)
                is OnEtNumberPhoneError -> setEtNumberPhoneError(formError)
                is OnEtPasswordError -> setEtPasswordError(formError)
                is OnEtSubscriptionDateError -> setSubscriptionDateError(formError)
                is OnSpnNetworkDeviceError -> setSpnNetworkDeviceError(formError)
                is OnSpnPlanError -> setSpnPlanError(formError)
                is OnSpnPlaceError -> setSpnPlaceError(formError)
                is OnEtLocationError -> setEtLocationError(formError)
            }
        }
    }

    private fun setEtLocationError(formError: OnEtLocationError) {
        binding.etLocationSubscription.error = formError.error
    }

    private fun setSpnPlaceError(formError: OnSpnPlaceError) {
        binding.spnPlace.error = formError.error
    }

    private fun clearTlFirstNameErrors() {
        binding.tlFirstName.error = null
    }

    private fun setSpnPlanError(formError: OnSpnPlanError) {
        binding.spnPlan.error = formError.error
    }

    private fun setSpnNetworkDeviceError(formError: OnSpnNetworkDeviceError) {
        binding.spnDevice.error = formError.error
    }

    private fun setSubscriptionDateError(formError: OnEtSubscriptionDateError) {
        binding.tlSubscriptionDate.error = formError.error
    }

    private fun setEtPasswordError(formError: OnEtPasswordError) {
        binding.tlPassword.error = formError.error
    }

    private fun setEtNumberPhoneError(formError: OnEtNumberPhoneError) {
        binding.tlPhone.error = formError.error
    }

    private fun setEtLastNameError(formError: OnEtLastNameError) {
        binding.tlLastName.error = formError.error
    }

    private fun setEtFirstNameError(formError: OnEtFirstNameError) {
        binding.tlFirstName.error = formError.error
    }

    private fun setEtDniError(formError: OnEtDniError) {
        binding.tlDni.error = formError.error
    }

    private fun setEtAddressError(formError: OnEtAddressesError) {
        binding.tlAddress.error = formError.error
    }

    private fun registerSubscription() {
        val firstname = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val dni = binding.etDni.text.toString()
        val password = binding.etPassword.text.toString()
        val address = binding.etAddress.text.toString()
        val phoneNumber = binding.etPhone.text.toString()
        val planId = selectedPlan?.id ?: ""
        val networkDeviceId = selectedNetworkDevice?.id ?: ""
        val placeId = selectedPlace?.id ?: ""
        val location = GeoLocation(selectedLocation!!.latitude, selectedLocation!!.longitude)
        val technicianId = selectedTechnician?.id ?: ""

        val subscription = Subscription(
            firstName = firstname,
            lastName = lastName,
            dni = dni,
            password = password,
            address = address,
            phone = phoneNumber,
            planId = planId,
            networkDeviceId = networkDeviceId,
            placeId = placeId,
            location = location,
            technicianId = technicianId,
            subscriptionDate = selectedDate
        )

        viewModel.registerSubscription(subscription)
    }

    private fun showSucessDialog(subscriptionFirstName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Te Subscribiste con exito ")
        builder.setMessage(subscriptionFirstName)
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("La subscripcion no fue procesada")
        builder.setMessage(error)
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun setUpPlansSpinner(plans: List<Plan>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, plans)
        binding.etPlan.setAdapter(adapter)
        binding.etPlan.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedPlan = plans[pos]
            }
    }

    private fun setUpNetworkDeviceSpinner(networkDevices: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevices)
        binding.etNetworkDevice.setAdapter(adapter)
        binding.etNetworkDevice.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNetworkDevice = networkDevices[pos]
            }
    }

    private fun setUpPlaceSpinner(places: List<Place>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, places)
        binding.etPlace.setAdapter(adapter)
        binding.etPlace.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedPlace = places[pos]
            }
    }

    private fun setUpTechnicianSpinner(technicians: List<Technician>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, technicians)
        binding.etTechnician.setAdapter(adapter)
        binding.etTechnician.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedTechnician = technicians[pos]
            }
    }

}
