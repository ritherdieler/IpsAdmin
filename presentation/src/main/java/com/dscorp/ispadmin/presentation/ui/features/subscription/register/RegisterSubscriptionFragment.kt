package com.dscorp.ispadmin.presentation.ui.features.subscription.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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
        binding.ivRefresh.setOnClickListener {
            viewModel.getOnuData()
        }
        observeState()
        observeMapDialogResult()

        setTextWatchersToStringFields()
        setInstallationTypeRadioGroupListener()
        binding.lvAditionalNetworkDevices.adapter = additionalDevicesAdapter
        binding.rgInstallationType.check(R.id.rbFiber)
        binding.btnRegisterSubscription.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            viewModel.registerSubscription()
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

        return binding.root
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
                    viewModel.installationType = InstallationType.FIBER
                    viewModel.getFiberDevices()
                    binding.spnNapBox.visibility = View.VISIBLE
                    binding.tlOnu.visibility = View.VISIBLE
                    binding.ivRefresh.visibility= View.VISIBLE
                }
                R.id.rbWireless -> {
                    viewModel.installationType = InstallationType.WIRELESS
                    viewModel.getWirelessDevices()
                    binding.spnNapBox.visibility = View.GONE
                    binding.tlOnu.visibility = View.GONE
                    binding.ivRefresh.visibility= View.GONE
                }
            }
            moveScrollViewToBottom()
        }
    }

    private fun resetNapBoxSpinner() {
        viewModel.napBoxField.value = null
        binding.etNapBox.setText("")
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
                is LoadingLogin -> binding.btnRegisterSubscription.isEnabled = !response.loading
                is OnOnuDataFound -> {
                    binding.acOnu.populate(response.onus) {
                        viewModel.onuField.value = it
                    }
                }
                is OnuDataError -> showErrorDialog(response.error)
            }
        }

    }

    private fun showConfirmationDialog(response: RegisterSubscriptionSuccess) {
        showCrossDialog(
            getString(R.string.subscription_register_success, response.subscription.ip.toString())
        ) {
            val action = RegisterSubscriptionFragmentDirections.saveSubscriptionToDashboard()
            findNavController().navigate(action)
            //navigate pop
            findNavController().popBackStack(R.id.nav_subscription, true)
        }
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
        binding.etTechnician.populate(response.technicians) {
            viewModel.technicianField.value = it
        }
        binding.etNapBox.populate(response.napBoxes) {
            viewModel.napBoxField.value = it
        }
        binding.etHostDevice.populate(response.hostNetworkDevices) {
            viewModel.hostDeviceField.value = it
        }
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
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val formattedDate = formatter.format(it)
            binding.etSubscriptionDate.setText(formattedDate)
        }

        datePicker.show(childFragmentManager, "DatePicker")

    }

    private fun showSuccessDialog(response: RegisterSubscriptionSuccess) {
        showSuccessDialog("El registro numero ${response.subscription.dni} ah sido registrado correctamente")
    }

}

