package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentRegisterSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.extension.fill
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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit


class RegisterSubscriptionFragment : BaseFragment() {
    private val binding by lazy { FragmentRegisterSubscriptionBinding.inflate(layoutInflater) }
    private val viewModel: SubscriptionViewModel by viewModel()
    private val additionalDevicesAdapter by lazy {
        ArrayAdapter<NetworkDevice>(
            requireContext(),
            android.R.layout.simple_spinner_item
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        viewModel.getFormData()
        observeResponse()

        binding.lvAditionalNetworkDevices.adapter = additionalDevicesAdapter

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
            resetCpeSpinner()
            resetAdditionalDevicesUiState()
            binding.tlAditonalNetworkDevices.visibility = View.VISIBLE
            binding.tlNetworkDeviceOne.visibility = View.VISIBLE
            binding.chkAdditionalDevices.visibility = View.VISIBLE

            when (checkedId) {
                R.id.rbFiber -> {
                    viewModel.installationType = InstallationType.FIBER
                    viewModel.getFiberDevices()
                    binding.spnNapBox.visibility = View.VISIBLE
                }
                R.id.rbWireless -> {
                    viewModel.installationType = InstallationType.WIRELESS
                    viewModel.getWirelessDevices()
                    binding.spnNapBox.visibility = View.GONE
                }
            }
            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding.chkAdditionalDevices.setOnCheckedChangeListener { _, isChecked ->
            resetAdditionalDevicesUiState()

            if (isChecked) {
                binding.llAdditionalDevices.visibility = View.VISIBLE
            } else {
                binding.llAdditionalDevices.visibility = View.GONE
            }
            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding.btnAddAditionalNetworkDevices.setOnClickListener {
            viewModel.addSelectedAdditionalNetworkDeviceToList()
            additionalDevicesAdapter.clear()
            additionalDevicesAdapter.addAll(viewModel.additionalNetworkDevicesList)
        }

        observeMapDialogResult()

        return binding.root
    }

    private fun resetAdditionalDevicesUiState() {
        viewModel.resetAdditionalDevicesValues()
        additionalDevicesAdapter.clear()
        binding.acAditionalNetworkDevices.setText("")
    }

    private fun resetCpeSpinner() {
        viewModel.selectedNetworkDeviceOne = null
        binding.etNetworkDeviceOne.setText("")
    }

    private fun observeMapDialogResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("location")
            ?.observe(viewLifecycleOwner) {
                onLocationSelected(it)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun onLocationSelected(it: LatLng) {
        viewModel.selectedLocation = it
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
        setupAdditionNetworkDeviceSpinner(response.networkDevices)
    }

    private fun setupAdditionNetworkDeviceSpinner(networkDevices: List<NetworkDevice>) {
        binding.acAditionalNetworkDevices.fill(networkDevices) {
            lifecycleScope.launch {
                viewModel.selectedAdditionalDevice.value = it
            }
        }
    }

    private fun registerSubscription() {

        val subscription = Subscription(
            firstName = binding.etFirstName.text.toString(),
            lastName = binding.etLastName.text.toString(),
            dni = binding.etDni.text.toString(),
            password = binding.etPassword.text.toString(),
            address = binding.etAddress.text.toString(),
            phone = binding.etPhone.text.toString(),
        )
        viewModel.registerSubscription(subscription)
    }

    private fun setUpPlansSpinner(plans: List<Plan>) {
        binding.etPlan.fill(plans) {
            viewModel.selectedPlan = it
        }
    }

    private fun setUpCpeDeviceSpinner(networkDevices: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevices)

        binding.etNetworkDeviceOne.setAdapter(adapter)
        binding.etNetworkDeviceOne.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                viewModel.selectedNetworkDeviceOne = networkDevices[pos]
            }
    }

    private fun setUpHostNetworkDeviceSpinners(networkDevice: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevice)

        binding.etHostDevice.setAdapter(adapter)
        binding.etHostDevice.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                viewModel.selectedHostNetworkDevice = networkDevice[pos]
            }
    }

    private fun setUpPlaceSpinner(places: List<Place>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, places)
        binding.etPlace.setAdapter(adapter)
        binding.etPlace.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                viewModel.selectedPlace = places[pos]
            }
    }

    private fun setUpTechnicianSpinner(technicians: List<Technician>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, technicians)
        binding.etTechnician.setAdapter(adapter)
        binding.etTechnician.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                viewModel.selectedTechnician = technicians[pos]
            }
    }

    private fun setUpNapBoxSpinner(napBoxes: List<NapBox>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, napBoxes)
        binding.etNapBox.setAdapter(adapter)
        binding.etNapBox.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                viewModel.selectedNapBox = napBoxes[pos]
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
            viewModel.selectedDate = it
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