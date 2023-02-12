package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionFormError
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionCleanForm.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionFormError.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.example.cleanarchitecture.domain.domain.entity.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

class RegisterSubscriptionFragment : BaseFragment() {
    private val binding by lazy { FragmentSubscriptionBinding.inflate(layoutInflater) }
    private var selectedDate: Long = 0
    private var selectedLocation: LatLng? = null
    private var selectedPlan: Plan? = null
    private var selectedNetworkDeviceOne: NetworkDevice? = null
    private var selectedNetworkDeviceTwo: NetworkDevice? = null
    private var selectedHostNetworkDevice: NetworkDevice? = null
    private var selectedPlace: Place? = null
    private var selectedTechnician: Technician? = null
    private var selectedNapBox: NapBox? = null
    private val viewModel: SubscriptionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.getFormData()
        observeResponse()
        observeFormError()
        observeCleanForm()

        binding.btSubscribirse.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            registerSubscription()
        }

        binding.etSubscriptionDate.setOnClickListener {
            showDatePicker()
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
        viewModel.registerSubscriptionUiState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is RegisterSubscriptionSuccess -> showSuccessDialog(response)
                is RegisterSubscriptionError -> showErrorDialog(response.error)
                is FormDataFound -> fillFormSpinners(response)
                is FormDataError -> showErrorDialog(response.error)
            }
        }
    }

    private fun showSuccessDialog(response: RegisterSubscriptionSuccess) {
        showSuccessDialog("El registro numero ${response.subscription.dni} ah sido registrado correctamente")
    }

    private fun fillFormSpinners(response: FormDataFound) {
        setUpPlansSpinner(response.plans)
        setUpNetworkDeviceSpinners(response.networkDevices)
        setUpPlaceSpinner(response.places)
        setUpTechnicianSpinner(response.technicians)
        setUpNapBoxSpinner(response.napBoxes)
        setUpHostNetworkDeviceSpinners(response.hostNetworkDevices)
    }

    private fun observeFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtAddressesError -> setEtAddressError(formError)
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
                is OnDniIsInvalidError -> setDniIsInvalidError(formError)
                is OnPhoneIsInvalidError -> binding.tlPhone.error = formError.error
                is OnPasswordIsInvalidError -> binding.tlPassword.error = formError.error
                is OnSpnTechnicianError -> binding.spnTechnician.error = formError.error
                is OnEtFirstNameIsInvalidError -> binding.tlFirstName.error = formError.error
                is OnEtLastNameIsInvalidError -> binding.tlLastName.error = formError.error
                is HostDeviceError -> binding.spnHostDevice.error = formError.error
            }
        }
    }

    private fun setDniIsInvalidError(formError: SubscriptionFormError) {
        binding.tlDni.error = formError.error
    }

    private fun observeCleanForm() {
        viewModel.cleanFormLiveData.observe(viewLifecycleOwner) { cleanForm ->
            when (cleanForm) {
                is OnEtDniHasNotErrors -> clearTlDniError()
                is OnEtFirstNameHasNotErrors -> clearTlFirstNameErrors()
                is OnEtLastNameHasNotErrors -> binding.tlLastName.error = null
                is OnEtPasswordHasNotErrors -> binding.tlPassword.error = null
                is OnEtAddressHasNotErrors -> binding.tlAddress.error = null
                is OnEtPhoneHasNotErrors -> binding.tlPhone.error = null
                OnEtSubscriptionDateNotErrors -> binding.tlSubscriptionDate.error = null
                OnEtPlanNotErrors -> binding.spnPlan.error = null
                OnEtNetworkDeviceNotErrors -> binding.spnNetworkDeviceOne.error = null
                OnEtNapBoxNotErrors -> binding.spnNapBox.error = null
                OnEtPlaceNotErrors -> binding.spnPlace.error = null
                OnEtTechnicianNotErrors -> binding.spnTechnician.error = null
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
        binding.spnNetworkDeviceOne.error = formError.error
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

        val installedDevices  = mutableListOf<Int>()
        selectedNetworkDeviceOne?.let { it.id?.let { it1 -> installedDevices.add(it1) } }
        selectedNetworkDeviceTwo?.let { it.id?.let { it1 -> installedDevices.add(it1) } }

        val subscription = Subscription(
            firstName = binding.etFirstName.text.toString(),
            lastName = binding.etLastName.text.toString(),
            dni = binding.etDni.text.toString(),
            password = binding.etPassword.text.toString(),
            address = binding.etAddress.text.toString(),
            phone = binding.etPhone.text.toString(),
            planId = selectedPlan?.id ?: "",
            networkDeviceIds = installedDevices,
            placeId = selectedPlace?.id ?: "",
            location = GeoLocation(
                selectedLocation?.latitude ?: 0.0,
                selectedLocation?.longitude ?: 0.0
            ),
            technicianId = selectedTechnician?.id ?: "",
            napBoxId = selectedNapBox?.id ?: "",
            subscriptionDate = selectedDate,
            hostDeviceId = selectedHostNetworkDevice?.id ?:0,
        )
        viewModel.registerSubscription(subscription)
    }

    private fun setUpPlansSpinner(plans: List<Plan>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, plans)
        binding.etPlan.setAdapter(adapter)
        binding.etPlan.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedPlan = plans[pos]
            }
    }

    private fun setUpNetworkDeviceSpinners(networkDevices: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevices)

        binding.etNetworkDeviceOne.setAdapter(adapter)
        binding.etNetworkDeviceOne.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNetworkDeviceOne = networkDevices[pos]
            }

        binding.etNetworkDeviceTwo.setAdapter(adapter)
        binding.etNetworkDeviceTwo.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNetworkDeviceTwo = networkDevices[pos]
            }
    }

    private fun setUpHostNetworkDeviceSpinners(networkDevice: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevice)

        binding.etHostDevice.setAdapter(adapter)
        binding.etHostDevice.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedHostNetworkDevice = networkDevice[pos]
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        val dateValidatorMin = DateValidatorPointForward.from(
            Calendar.getInstance().timeInMillis - 15.days.toLong(DurationUnit.MILLISECONDS)
        )

        val dateValidatorMax =
            DateValidatorPointBackward.before(Calendar.getInstance().timeInMillis)

        val dateValidator =
            CompositeDateValidator.allOf(listOf(dateValidatorMin, dateValidatorMax))

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(dateValidator)
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener {
            selectedDate = it
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = formatter.format(calendar.timeInMillis)
            binding.etSubscriptionDate.setText(formattedDate)
        }

        datePicker.show(childFragmentManager, "DatePicker")
    }
}
