package com.dscorp.ispadmin.presentation.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class SubscriptionFragment : Fragment() {
    lateinit var binding: FragmentSubscriptionBinding
    var selectedDate: Long = 0
    private var selectedLocation: LatLng? = null
    var selectedPlan: Plan? = null
    var selectedNetworkDevice: NetworkDevice? = null
    var selectedPlace: Place? = null
    private var selectedTechnician: Technician? = null
    var selectedNapBox: NapBox? = null
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
            val minDate = Calendar.getInstance().apply {
                set(2023, 0, 15) // 0 = January, 15 = 15th
            }.timeInMillis
            val maxDate = MaterialDatePicker.todayInUtcMilliseconds()
            val constraintsBuilder = CalendarConstraints.Builder()
                .setEnd(maxDate)
                .setStart(minDate)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
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
        val edPhone: EditText = binding.etPhone

        edPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length < 9) {
                        viewModel.formErrorLiveData.postValue(OnEtNumberPhoneError("La cantidad mínima de caracteres para el número de teléfono es de 9 (nueve)"))
                        return
                    } else {
                        viewModel.formErrorLiveData.postValue(OnEtNumberPhoneError(""))
                    }
                }
            }
        })
        val edDni: EditText = binding.etDni

        edDni.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length < 8) {
                        viewModel.formErrorLiveData.postValue(OnEtDniError("La cantidad mínima de caracteres para el número de teléfono es de 8"))

                    } else {
                        viewModel.formErrorLiveData.postValue(OnEtDniHasNotError)
                    }
                }
            }
        })

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
        setUpNapBoxSpinner(response.napBoxs)
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
                is OnSpnNapBoxError -> setSpnNapBoxError(formError)
                OnEtDniHasNotError -> clearTlDniError()
            }
        }
    }

    private fun clearTlDniError() {
        binding.tlDni.error = null
    }

    private fun setSpnNapBoxError(formError: OnSpnNapBoxError) {
        binding.spnNapBox.error = formError.error
    }

    private fun setEtLocationError(formError: OnEtLocationError) {
        binding.tlLocationSubscription.error = formError.error
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

        val subscription = Subscription(
            firstName = binding.etFirstName.text.toString(),
            lastName = binding.etLastName.text.toString(),
            dni = binding.etDni.text.toString(),
            password = binding.etPassword.text.toString(),
            address = binding.etAddress.text.toString(),
            phone = binding.etPhone.text.toString(),
            planId = selectedPlan?.id ?: "",
            networkDeviceId = selectedNetworkDevice?.id ?: "",
            placeId = selectedPlace?.id ?: "",
            location = GeoLocation(selectedLocation!!.latitude, selectedLocation!!.longitude),
            technicianId = selectedTechnician?.id ?: "",
            napBoxId = selectedNapBox?.id ?: "",
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

    private fun setUpNapBoxSpinner(napBoxes: List<NapBox>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, napBoxes)
        binding.etNapBox.setAdapter(adapter)
        binding.etNapBox.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNapBox = napBoxes[pos]
            }
    }
}
