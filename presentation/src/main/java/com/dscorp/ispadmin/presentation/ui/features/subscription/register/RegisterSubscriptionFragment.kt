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
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentRegisterSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.*
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionFormErrorUiState.*
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUiState.*
import com.example.cleanarchitecture.domain.domain.entity.*
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
    private val viewModel: SubscriptionViewModel by viewModel()
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
            if (isGranted) {
                getCurrentLocation()
            } else {
                if (rationaleShown) openLocationSettings(requireActivity())
            }
        }

    private fun getCurrentLocation() {
        binding.ivMyLocation.setImageResource(R.drawable.ic_rotate_right)
        binding.ivMyLocation.animateRotate360InLoop()
        fusedLocationClient.getCurrentLocation {
            binding.etLocationSubscription.setText("${it.latitude}, ${it.longitude}")
            viewModel.locationField.value = it
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

        setTextWatchersToStringFields()
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

        viewModel.subscriptionDateField.value = currentTimeMillis
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
        viewModel.napBoxField.value = null
        binding.acNapBox.setText("")
    }

    private fun moveScrollViewToBottom() {
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun setTextWatchersToStringFields() {
        binding.etFirstName.doOnTextChanged { text, start, before, count ->
            viewModel.firstNameField.value = text.toString()
        }
        binding.etLastName.doOnTextChanged { text, start, before, count ->
            viewModel.lastNameField.value = text.toString()
        }
        binding.etDni.doOnTextChanged { text, start, before, count ->
            viewModel.dniField.value = text.toString()
        }
        binding.etDni.doOnTextChanged { text, start, before, count ->
            viewModel.dniField.value = text.toString()
        }
        binding.etAddress.doOnTextChanged { text, start, before, count ->
            viewModel.addressField.value = text.toString()
        }
        binding.etPhone.doOnTextChanged { text, start, before, count ->
            viewModel.phoneField.value = text.toString()
        }
        binding.etPriceSubscription.doOnTextChanged { text, start, before, count ->
            viewModel.priceField.value =
                if (text.isNullOrEmpty()) null else text.toString().toDouble()
        }
        binding.etCupon.doOnTextChanged { text, start, before, count ->
            viewModel.couponField.value = text.toString()
        }
        binding.etNote.doOnTextChanged { text, start, before, count ->
            viewModel.noteField.value = text.toString()
        }
    }

    private fun resetAdditionalDevicesUiState() {
        viewModel.resetAdditionalDevicesValues()
        additionalDevicesAdapter.clear()
        binding.acAditionalNetworkDevices.setText("")
    }

    private fun resetCpeSpinner() {
        viewModel.cpeDeviceField.value = null
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
        viewModel.locationField.value = it
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
                is LoadingLogin -> binding.ProgressButton.setProgressButtonDisable(response.loading)
                is OnOnuDataFound -> {
                    binding.acOnu.populate(response.onus) {
                        viewModel.onuField.value = it
                    }
                }
                is OnuDataError -> showErrorDialog(response.error)
                is RefreshingOnus -> showOnusRefreshing(response.isRefreshing)
                is ButtomProgressBar -> binding.ProgressButton.setProgressBarVisible(response.loading)
            }
        }
    }

    private fun showOnusRefreshing(refreshing: Boolean) {
        if (refreshing) {
            binding.ivRefresh.isEnabled = false
            val rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotateAnimation.duration = 1000
            rotateAnimation.repeatCount = Animation.INFINITE
            binding.ivRefresh.startAnimation(rotateAnimation)
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
        binding.viewLoading.visibility =
            if (response.loading) View.VISIBLE else View.GONE
        binding.viewContainer.visibility =
            if (response.loading) View.GONE else View.VISIBLE
    }

    private fun showCouponActivationResponse(couponIsValid: Boolean) {
        if (couponIsValid) Toast.makeText(requireContext(), "Cupon valido", Toast.LENGTH_SHORT)
            .show()
        else Toast.makeText(requireContext(), "Cupon no valido", Toast.LENGTH_SHORT).show()
    }

    private fun fillCpeDeviceSpinner(devices: List<NetworkDevice>) {
        binding.etCpeNetworkDevice.populate(devices) {
            viewModel.cpeDeviceField.value = it
        }
    }

    private fun fillFormSpinners(response: FormDataFound) {
        binding.etPlan.populate(response.plans) {
            viewModel.planField.value = it
        }
        binding.etPlace.populate(response.places) {
            viewModel.placeField.value = it
        }
        pupulateTechnicianSpinner(response)
        binding.acNapBox.populate(response.napBoxes) {
            viewModel.napBoxField.value = it
        }
        populateHostDeviceSprinner(response)
        binding.acAditionalNetworkDevices.populate(response.networkDevices) {
            lifecycleScope.launch {
                viewModel.selectedAdditionalDevice.value = it
            }
        }
        binding.acOnu.populate(response.unconfirmedOnus) {
            lifecycleScope.launch {
                viewModel.onuField.value = it
            }
        }
    }

    private fun pupulateTechnicianSpinner(response: FormDataFound) {
        binding.etTechnician.populate(response.technicians) {
            viewModel.technicianField.value = it
        }
        if (response.technicians.size == 1) {
            viewModel.technicianField.value = response.technicians[0]
            binding.etTechnician.setText(viewModel.technicianField.value.toString())
            binding.spnTechnician.isEnabled = false
            binding.spnTechnician.visibility = View.GONE
        }
    }

    private fun populateHostDeviceSprinner(response: FormDataFound) {
        binding.etHostDevice.populate(response.hostNetworkDevices) {
            viewModel.hostDeviceField.value = it
        }
        if (response.hostNetworkDevices.size == 1) {
            viewModel.hostDeviceField.value = response.hostNetworkDevices[0]
            binding.etHostDevice.setText(viewModel.hostDeviceField.value.toString())
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
            viewModel.subscriptionDateField.value = it
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

