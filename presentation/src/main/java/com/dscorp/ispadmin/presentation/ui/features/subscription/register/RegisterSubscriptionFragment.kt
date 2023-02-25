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
import com.dscorp.ispadmin.databinding.FragmentRegisterSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState.*
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
    private val binding by lazy { FragmentRegisterSubscriptionBinding.inflate(layoutInflater) }
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
    private var installationType: InstallationType? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.getFormData()
        observeResponse()
        observeFormError()

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


        binding.rgInstallationType.setOnCheckedChangeListener { _, checkedId ->
            binding.tlNetworkDeviceOne.visibility = View.VISIBLE
            when (checkedId) {
                R.id.rbFiber -> {
                    viewModel.getFiberDevices()
                    this.installationType = InstallationType.FIBER
                    binding.layoutWireless.visibility = View.GONE
                    binding.layoutFiber.visibility = View.VISIBLE
                }
                R.id.rbWireless -> {
                    viewModel.getWirelessDevices()
                    this.installationType = InstallationType.WIRELESS
                    binding.layoutWireless.visibility = View.VISIBLE
                    binding.layoutFiber.visibility = View.GONE
                }
            }
            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
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
                is FiberDevicesFound -> fillCpeDeviceSpinner(response.devices)
                is WirelessDevicesFound -> fillCpeDeviceSpinner(response.devices)
            }
        }
    }

    private fun fillCpeDeviceSpinner(devices: List<NetworkDevice>) {
        setUpCpeDeviceSpinner(devices)
    }

    private fun showSuccessDialog(response: RegisterSubscriptionSuccess) {
        showSuccessDialog("El registro numero ${response.subscription.dni} ah sido registrado correctamente")
    }

    private fun fillFormSpinners(response: FormDataFound) {
        setUpPlansSpinner(response.plans)
        setUpPlaceSpinner(response.places)
        setUpTechnicianSpinner(response.technicians)
        setUpNapBoxSpinner(response.napBoxes)
        setUpHostNetworkDeviceSpinners(response.hostNetworkDevices)
    }

    private fun observeFormError() {
        viewModel.registerFormErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtAddressesErrorRegisterUiState -> setEtAddressError(formError)
                is OnEtDniErrorRegisterUiState -> setEtDniError(formError)
                is OnEtFirstNameErrorRegisterUiState -> setEtFirstNameError(formError)
                is OnEtLastNameErrorRegisterUiState -> setEtLastNameError(formError)
                is OnEtNumberPhoneErrorRegisterUiState -> setEtNumberPhoneError(formError)
                is OnEtPasswordErrorRegisterUiState -> setEtPasswordError(formError)
                is OnEtRegisterSubscriptionDateErrorUiState -> setSubscriptionDateError(formError)
                is OnSpnNetworkDeviceErrorRegisterUiState -> setSpnNetworkDeviceError(formError)
                is OnSpnPlanErrorRegisterUiState -> setSpnPlanError(formError)
                is OnSpnPlaceErrorRegisterUiState -> setSpnPlaceError(formError)
                is OnEtLocationErrorRegisterUiState -> setEtLocationError(formError)
                is OnSpnNapBoxErrorRegisterUiState -> setSpnNapBoxError(formError)
                is OnDniIsInvalidErrorRegisterUiState -> setDniIsInvalidError(formError)
                is OnPhoneIsInvalidErrorRegisterUiState -> binding.tlPhone.error = formError.error
                is OnPasswordIsInvalidErrorRegisterUiState ->
                    binding.tlPassword.error =
                        formError.error
                is OnSpnTechnicianErrorRegisterUiState ->
                    binding.spnTechnician.error =
                        formError.error
                is OnEtFirstNameIsInvalidErrorRegisterUiState ->
                    binding.tlFirstName.error =
                        formError.error
                is OnEtLastNameIsInvalidErrorRegisterUiState ->
                    binding.tlLastName.error =
                        formError.error
                is HostUiStateDeviceErrorRegister -> binding.spnHostDevice.error = formError.error
                // CLEAN FORM
                is CleanEtDniHasNotErrors -> clearTlDniError()
                is CleanEtFirstNameHasNotErrors -> clearTlFirstNameErrors()
                is CleanEtLastNameHasNotErrors -> binding.tlLastName.error = null
                is CleanEtPasswordHasNotErrors -> binding.tlPassword.error = null
                is CleanEtAddressHasNotErrors -> binding.tlAddress.error = null
                is CleanEtPhoneHasNotErrors -> binding.tlPhone.error = null
                is CleanEtSubscriptionDateNotErrors -> binding.tlSubscriptionDate.error = null
                is CleanEtPlanNotErrors -> binding.spnPlan.error = null
                is CleanEtNetworkDeviceNotErrors -> binding.tlNetworkDeviceOne.error = null
                is CleanEtNapBoxNotErrors -> binding.spnNapBox.error = null
                is CleanEtPlaceNotErrors -> binding.spnPlace.error = null
                is CleanEtTechnicianNotErrors -> binding.spnTechnician.error = null
            }
        }
    }

    private fun setDniIsInvalidError(formError: RegisterSubscriptionFormErrorUiState) {
        binding.tlDni.error = formError.error
    }

    private fun clearTlDniError() {
        binding.tlDni.error = null
    }

    private fun setSpnNapBoxError(formError: OnSpnNapBoxErrorRegisterUiState) {
        binding.spnNapBox.error = formError.error
    }

    private fun setEtLocationError(formError: OnEtLocationErrorRegisterUiState) {
        binding.tlLocationSubscription.error = formError.error
    }

    private fun setSpnPlaceError(formError: OnSpnPlaceErrorRegisterUiState) {
        binding.spnPlace.error = formError.error
    }

    private fun clearTlFirstNameErrors() {
        binding.tlFirstName.error = null
    }

    private fun setSpnPlanError(formError: OnSpnPlanErrorRegisterUiState) {
        binding.spnPlan.error = formError.error
    }

    private fun setSpnNetworkDeviceError(formError: OnSpnNetworkDeviceErrorRegisterUiState) {
        binding.tlNetworkDeviceOne.error = formError.error
    }

    private fun setSubscriptionDateError(formError: OnEtRegisterSubscriptionDateErrorUiState) {
        binding.tlSubscriptionDate.error = formError.error
    }

    private fun setEtPasswordError(formError: OnEtPasswordErrorRegisterUiState) {
        binding.tlPassword.error = formError.error
    }

    private fun setEtNumberPhoneError(formError: OnEtNumberPhoneErrorRegisterUiState) {
        binding.tlPhone.error = formError.error
    }

    private fun setEtLastNameError(formError: OnEtLastNameErrorRegisterUiState) {
        binding.tlLastName.error = formError.error
    }

    private fun setEtFirstNameError(formError: OnEtFirstNameErrorRegisterUiState) {
        binding.tlFirstName.error = formError.error
    }

    private fun setEtDniError(formError: OnEtDniErrorRegisterUiState) {
        binding.tlDni.error = formError.error
    }

    private fun setEtAddressError(formError: OnEtAddressesErrorRegisterUiState) {
        binding.tlAddress.error = formError.error
    }

    private fun registerSubscription() {

        val installedDevices = mutableListOf<Int>()
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
            technicianId = selectedTechnician?.id,
            napBoxId = selectedNapBox?.id ?: "",
            subscriptionDate = selectedDate,
            hostDeviceId = selectedHostNetworkDevice?.id ?: 0,
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

    private fun setUpCpeDeviceSpinner(networkDevices: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevices)

        binding.etNetworkDeviceOne.setAdapter(adapter)
        binding.etNetworkDeviceOne.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNetworkDeviceOne = networkDevices[pos]
            }

//        binding.etNetworkDeviceTwo.setAdapter(adapter)
//        binding.etNetworkDeviceTwo.onItemClickListener =
//            AdapterView.OnItemClickListener { _, _, pos, _ ->
//                selectedNetworkDeviceTwo = networkDevices[pos]
//            }
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

enum class InstallationType {
    FIBER,
    WIRELESS
}