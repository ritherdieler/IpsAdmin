package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentRegisterSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.*
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.cleanarchitecture.domain.domain.entity.extensions.toFormattedDateString
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit


class RegisterSubscriptionFragment : BaseFragment() {
    private val binding by lazy { FragmentRegisterSubscriptionBinding.inflate(layoutInflater) }
    private val viewModel: RegisterSubscriptionViewModel by viewModel()
    private var rationaleShown = false
    private val additionalDevicesAdapter by lazy {
        ArrayAdapter<NetworkDevice>(
            requireContext(),
            android.R.layout.simple_spinner_item
        )
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted)
                checkGpsEnabled { getCurrentLocation() }
            else
                if (rationaleShown) openLocationSettings(requireActivity())
        }

    private fun getCurrentLocation() {
        binding.ivMyLocation.setImageResource(R.drawable.ic_rotate_right)
        binding.ivMyLocation.animateRotate360InLoop()
        fusedLocationClient.getCurrentLocation {
            binding.etLocationSubscription.setText("${it.latitude}, ${it.longitude}")
            viewModel.locationField.liveData.value = it
            binding.ivMyLocation.clearAnimation()
            binding.ivMyLocation.setImageResource(R.drawable.ic_my_location)
        }
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
        binding.ivRefresh.setOnClickListener {
            viewModel.getOnuData()
        }
        observeState()
        observeMapDialogResult()

        setInstallationTypeRadioGroupListener()
        binding.lvAditionalNetworkDevices.adapter = additionalDevicesAdapter
        binding.rgInstallationType.check(R.id.rbFiber)

        binding.ProgressButton.clickButtonProgress = {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            viewModel.registerSubscription()
        }
        binding.ivMyLocation.setOnClickListener {
            requestLocationPermission()
        }

        binding.etSubscriptionDate.setOnClickListener {
            showDatePicker()
        }

        binding.etLocationSubscription.setOnClickListener {
            findNavController().navigateSafe(R.id.action_nav_subscription_to_mapDialog)
        }

        binding.chkAdditionalDevices.setOnCheckedChangeListener { _, isChecked ->
            resetAdditionalDevicesUiState()
            if (isChecked) {
                binding.llAdditionalDevices.visibility = View.VISIBLE
            } else {
                binding.llAdditionalDevices.visibility = View.GONE
            }
            moveScrollViewToBottom()
        }

        binding.btnAddAditionalNetworkDevices.setOnClickListener {
            viewModel.addSelectedAdditionalNetworkDeviceToList()
            additionalDevicesAdapter.clear()
            additionalDevicesAdapter.addAll(viewModel.additionalNetworkDevicesList)
        }


        val currentTimeMillis = System.currentTimeMillis()

        viewModel.subscriptionDateField.liveData.value = currentTimeMillis
        binding.etSubscriptionDate.setText(currentTimeMillis.toFormattedDateString())
        binding.etSubscriptionDate.isEnabled = false
        binding.etSubscriptionDate.visibility = View.GONE
        return binding.root
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permiso de ubicación requerido")
                .setMessage("Se requiere el permiso de ubicación para obtener la ubicación actual")
                .setPositiveButton("OK") { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
            rationaleShown = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setInstallationTypeRadioGroupListener() {
        binding.rgInstallationType.setOnCheckedChangeListener { _, checkedId ->
            resetCpeSpinner()
            resetNapBoxSpinner()

            resetAdditionalDevicesUiState()
            binding.tlAditonalNetworkDevices.visibility = View.VISIBLE
            binding.tlCpeNetworkDevice.visibility = View.VISIBLE
            binding.chkAdditionalDevices.visibility = View.VISIBLE

            when (checkedId) {
                R.id.rbFiber -> {
                    viewModel.installationType.value = InstallationType.FIBER
                    viewModel.getFiberDevices()
                    binding.spnNapBox.visibility = View.VISIBLE
                    binding.tlOnu.visibility = View.VISIBLE
                    binding.ivRefresh.visibility = View.VISIBLE
                }
                R.id.rbWireless -> {
                    viewModel.installationType.value = InstallationType.WIRELESS
                    viewModel.getWirelessDevices()
                    binding.spnNapBox.visibility = View.GONE
                    binding.tlOnu.visibility = View.GONE
                    binding.ivRefresh.visibility = View.GONE
                }
            }
            moveScrollViewToBottom()
        }
    }

    private fun resetNapBoxSpinner() {
        viewModel.napBoxField.liveData.value = null
        binding.acNapBox.setText("")
    }

    private fun moveScrollViewToBottom() {
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun resetAdditionalDevicesUiState() {
        viewModel.resetAdditionalDevicesValues()
        additionalDevicesAdapter.clear()
        binding.acAditionalNetworkDevices.setText("")
    }

    private fun resetCpeSpinner() {
        viewModel.cpeDeviceField.liveData.value = null
        binding.etCpeNetworkDevice.setText("")
    }

    private fun observeMapDialogResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("location")
            ?.observe(viewLifecycleOwner) {
                onLocationSelected(it)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun onLocationSelected(it: LatLng) {
        viewModel.locationField.liveData.value = it
        binding.etLocationSubscription.setText("${it.latitude}, ${it.longitude}")
    }

    private fun observeState() = lifecycleScope.launch {
        viewModel.registerSubscriptionUiState.collect { response ->
            when (response) {
                is RegisterSubscriptionSuccess -> showConfirmationDialog(response)
                is RegisterSubscriptionError -> showErrorDialog(response.error)
                is FormDataFound -> fillFormSpinners(response)
                is FormDataError -> showErrorDialog(response.error)
                is FiberDevicesFound -> fillCpeDeviceSpinner(response.devices)
                is WirelessDevicesFound -> fillCpeDeviceSpinner(response.devices)
                is CouponIsValid -> showCouponActivationResponse(response.isValid)
                is GenericError -> showErrorDialog(response.error)
                is LoadingData -> showLoadingStatus(response)
                is OnOnuDataFound -> populatreOnuSpinner(response)
                is OnuDataError -> showErrorDialog(response.error)
                is RefreshingOnus -> showOnusRefreshing(response.isRefreshing)
                is ButtomProgressBar -> binding.ProgressButton.setProgressBarVisible(response.loading)
            }
        }
    }

    private fun populatreOnuSpinner(response: OnOnuDataFound) {
        binding.acOnu.populate(response.onus) {
            viewModel.onuField.liveData.value = it
        }
    }

    private fun showOnusRefreshing(refreshing: Boolean) {
        if (refreshing) {
            binding.ivRefresh.isEnabled = false
            binding.ivRefresh.animateRotate360InLoop()
        } else {
            binding.ivRefresh.isEnabled = true
            binding.ivRefresh.clearAnimation()
        }
    }

    private fun showConfirmationDialog(response: RegisterSubscriptionSuccess) {
        val action = RegisterSubscriptionFragmentDirections.saveSubscriptionToDashboard()
        showCrossDialog(
            getString(R.string.subscription_register_success, response.subscription.ip.toString()),
            closeButtonClickListener = { findNavController().navigate(action) },
            positiveButtonClickListener = { findNavController().navigate(action) }
        )
    }

    private fun showLoadingStatus(response: LoadingData) {
        binding.viewLoading.visibility = if (response.loading) View.VISIBLE else View.GONE
        binding.viewContainer.visibility = if (response.loading) View.GONE else View.VISIBLE
    }

    private fun showCouponActivationResponse(couponIsValid: Boolean) {
        if (couponIsValid) Toast.makeText(requireContext(), "Cupon valido", Toast.LENGTH_SHORT)
            .show()
        else Toast.makeText(requireContext(), "Cupon no valido", Toast.LENGTH_SHORT).show()
    }

    private fun fillCpeDeviceSpinner(devices: List<NetworkDevice>) {
        binding.etCpeNetworkDevice.populate(devices) {
            viewModel.cpeDeviceField.liveData.value = it
        }
    }

    private fun fillFormSpinners(response: FormDataFound) {
        binding.etPlan.populate(response.plans) {
            viewModel.planField.liveData.value = it
        }
        binding.etPlace.populate(response.places) {
            viewModel.placeField.liveData.value = it
        }
        populateTechnicianSpinner(response)
        binding.acNapBox.populate(response.napBoxes) {
            viewModel.napBoxField.liveData.value = it
        }
        populateHostDeviceSprinner(response)
        binding.acAditionalNetworkDevices.populate(response.networkDevices) {
            viewModel.selectedAdditionalDevice.value = it
        }
        binding.acOnu.populate(response.unconfirmedOnus) {
            viewModel.onuField.liveData.value = it
        }
    }

    private fun populateTechnicianSpinner(response: FormDataFound) {
        binding.etTechnician.populate(response.technicians) {
            viewModel.technicianField.liveData.value = it
        }
        if (response.technicians.size == 1) {
            viewModel.technicianField.liveData.value = response.technicians[0]
            binding.etTechnician.setText(viewModel.technicianField.liveData.value.toString())
            binding.spnTechnician.isEnabled = false
            binding.spnTechnician.visibility = View.GONE
        }
    }

    private fun populateHostDeviceSprinner(response: FormDataFound) {
        binding.etHostDevice.populate(response.hostNetworkDevices) {
            viewModel.hostDeviceField.liveData.value = it
        }
        if (response.hostNetworkDevices.size == 1) {
            viewModel.hostDeviceField.liveData.value = response.hostNetworkDevices[0]
            binding.etHostDevice.setText(viewModel.hostDeviceField.liveData.value.toString())
            binding.spnHostDevice.isEnabled = false
            binding.spnHostDevice.visibility = View.GONE
        }

    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        val dateValidatorMin = DateValidatorPointForward.from(
            calendar.timeInMillis - 15.days.toLong(DurationUnit.MILLISECONDS)
        )

        val dateValidatorMax = DateValidatorPointBackward.before(calendar.timeInMillis)

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
            viewModel.subscriptionDateField.liveData.value = it
            val formattedDate = it.toFormattedDateString()
            binding.etSubscriptionDate.setText(formattedDate)
        }
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun openLocationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

}

