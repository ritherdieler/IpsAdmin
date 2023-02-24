package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentEditSubscriptionBinding
import com.dscorp.ispadmin.presentation.extension.analytics.AnalyticsConstants
import com.dscorp.ispadmin.presentation.extension.analytics.sendTouchButtonEvent
import com.dscorp.ispadmin.presentation.extension.navigateSafe
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionFormErrorUiState.*
import com.example.cleanarchitecture.domain.domain.entity.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EditSubscriptionFragment : BaseFragment() {
    private val args by navArgs<EditSubscriptionFragmentArgs>()
    private val binding by lazy { FragmentEditSubscriptionBinding.inflate(layoutInflater) }
    private var selectedDate: Long = 0
    private var selectedLocation: LatLng? = null
    private var selectedPlan: Plan? = null
    private var selectedNetworkDeviceOne: NetworkDevice? = null
    private var selectedNetworkDeviceTwo: NetworkDevice? = null
    private var selectedHostNetworkDevice: NetworkDevice? = null
    private var selectedPlace: Place? = null
    private var selectedNapBox: NapBox? = null
    private val viewModel: SubscriptionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.subscription = args.subscription
        viewModel.getFormData()
        fillFormWithSubscriptionData()
        observeResponse()
        observeFormError()

        binding.btSubscribirse.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            editSubscription()
        }

        binding.etLocationSubscription.setOnClickListener {
            findNavController().navigateSafe(R.id.action_editSubscriptionFragment_to_mapDialog)
        }

        observeMapDialogResult()

        return binding.root
    }

    private fun fillFormWithSubscriptionData() {
        binding.etFirstName.setText(viewModel.subscription?.firstName)
        binding.etLastName.setText(viewModel.subscription?.lastName)
        binding.etAddress.setText(viewModel.subscription?.address)
        binding.etPhone.setText(viewModel.subscription?.phone)
        binding.etDni.setText(viewModel.subscription?.dni)
        binding.etPassword.setText(viewModel.subscription?.password)
        binding.etLocationSubscription.setText("${viewModel.subscription?.location?.latitude}, ${viewModel.subscription?.location?.longitude}")
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
        viewModel.editSubscriptionUiState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is EditSubscriptionUiState.EditSubscriptionSuccess -> showSuccessDialog("Suscripción editada con éxito")
                is EditSubscriptionUiState.EditSubscriptionError -> showErrorDialog(response.error)
                is EditSubscriptionUiState.FormDataFound -> fillFormSpinners(response)
                is EditSubscriptionUiState.FetchFormDataError -> showErrorDialog(response.error)
            }
        }
    }

    private fun fillFormSpinners(response: EditSubscriptionUiState.FormDataFound) {
        setUpPlansSpinner(response.plans)
/*
        setUpNetworkDeviceSpinners(response.networkDevices)
*/
        setUpPlaceSpinner(response.places)
        setUpNapBoxSpinner(response.napBoxes)
        setUpHostNetworkDeviceSpinners(response.hostNetworkDevices)
    }

    private fun observeFormError() {
        viewModel.editFormErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtFirstNameErrorRegisterUiState -> binding.tlFirstName.error = formError.error
                is OnEtLastNameErrorRegisterUiState -> binding.tlFirstName.error = formError.error
                is OnEtAddressesErrorRegisterUiState -> setEtAddressError(formError)
                is OnEtPhoneErrorRegisterUiState -> setEtNumberPhoneError(formError)
                is OnEtPasswordErrorRegisterUiState -> setEtPasswordError(formError)
                is OnSpnNetworkDeviceErrorRegisterUiState -> setSpnNetworkDeviceError(formError)
                is OnSpnPlanErrorRegisterUiState -> setSpnPlanError(formError)
                is OnSpnPlaceErrorRegisterUiState -> setSpnPlaceError(formError)
                is OnEtLocationErrorRegisterUiState -> setEtLocationError(formError)
                is OnSpnNapBoxErrorRegisterUiState -> setSpnNapBoxError(formError)
                is OnPhoneIsInvalidErrorRegisterUiState -> binding.tlPhone.error = formError.error
                is OnPasswordIsInvalidErrorRegisterUiState ->
                    binding.tlPassword.error =
                        formError.error
                is HostDeviceError -> binding.spnHostDevice.error = formError.error

                // CLEAN FORM
                is CleanEtPasswordHasNotErrors -> binding.tlPassword.error = null
                is CleanEtAddressHasNotErrors -> binding.tlAddress.error = null
                is CleanEtPhoneHasNotErrors -> binding.tlPhone.error = null
                is CleanEtPlanNotErrors -> binding.spnPlan.error = null
                is CleanEtNetworkDeviceNotErrors -> binding.spnNetworkDeviceOne.error = null
                is CleanEtNapBoxNotErrors -> binding.spnNapBox.error = null
                is CleanEtPlaceNotErrors -> binding.spnPlace.error = null
                is CleanEtFirstNameHasNotErrors -> binding.etFirstName.error = null
                is CleanEtLastNameHasNotErrors -> binding.etLastName.error = null
            }
        }
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

    private fun setSpnPlanError(formError: OnSpnPlanErrorRegisterUiState) {
        binding.spnPlan.error = formError.error
    }

    private fun setSpnNetworkDeviceError(formError: OnSpnNetworkDeviceErrorRegisterUiState) {
        binding.spnNetworkDeviceOne.error = formError.error
    }

    private fun setEtPasswordError(formError: OnEtPasswordErrorRegisterUiState) {
        binding.tlPassword.error = formError.error
    }

    private fun setEtNumberPhoneError(formError: OnEtPhoneErrorRegisterUiState) {
        binding.tlPhone.error = formError.error
    }

    private fun setEtAddressError(formError: OnEtAddressesErrorRegisterUiState) {
        binding.tlAddress.error = formError.error
    }

    private fun editSubscription() {

        val installedDevices = mutableListOf<Int>()
        selectedNetworkDeviceOne?.let { it.id?.let { it1 -> installedDevices.add(it1) } }
        selectedNetworkDeviceTwo?.let { it.id?.let { it1 -> installedDevices.add(it1) } }

        var location: GeoLocation?
        location = args.subscription.location
        selectedLocation?.let {
            location = GeoLocation(it.latitude, it.longitude)
        }

        val subscription = Subscription(
            id = args.subscription.id ?: 0,
            firstName = binding.etFirstName.text.toString(),
            lastName = binding.etLastName.text.toString(),
            password = binding.etPassword.text.toString(),
            address = binding.etAddress.text.toString(),
            dni = binding.etDni.text.toString(),
            phone = binding.etPhone.text.toString(),
            planId = selectedPlan?.id ?: "",
            networkDeviceIds = installedDevices,
            placeId = selectedPlace?.id ?: "",
            location = location,
            technicianId = args.subscription.technician?.id,
            napBoxId = selectedNapBox?.id ?: "",
            subscriptionDate = selectedDate,
            hostDeviceId = selectedHostNetworkDevice?.id ?: 0,
            isNew = args.subscription.new,
            serviceIsSuspended = args.subscription.serviceIsSuspended,

            )
        viewModel.editSubscription(subscription)
    }

    private fun setUpPlansSpinner(plans: List<Plan>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, plans)
        binding.etPlan.setAdapter(adapter)
        binding.etPlan.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedPlan = plans[pos]
            }
        if (plans.isNotEmpty()) {
            val currentPlanIndex = plans.indexOf(viewModel.subscription?.plan)
            binding.etPlan.setText(viewModel.subscription?.plan?.name, false)
            binding.etPlan.setSelection(currentPlanIndex)
            selectedPlan = plans[currentPlanIndex]
        }
    }

/*
    private fun setUpNetworkDeviceSpinners(networkDevices: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevices)

        binding.etNetworkDeviceOne.setAdapter(adapter)
        binding.etNetworkDeviceOne.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNetworkDeviceOne = networkDevices[pos]
            }
           if (networkDevices.isNotEmpty()) {
               val currentPlanIndex=networkDevices.indexOf(viewModel.subscription?.networkDevices)
               binding.etNetworkDeviceOne.setText(viewModel.subscription?.networkDevices?.name,false)
               binding.etNetworkDeviceOne.setSelection(currentPlanIndex)
               selectedNetworkDeviceOne = networkDevices[currentPlanIndex]
           }

        binding.etNetworkDeviceTwo.setAdapter(adapter)
        binding.etNetworkDeviceTwo.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedNetworkDeviceTwo = networkDevices[pos]
            }
*/
/*        if (networkDevices.isNotEmpty()) {
            val currentPlanIndex=networkDevices.indexOf(viewModel.subscription?.networkDevice)
            binding.etNetworkDeviceTwo.setText(viewModel.subscription?.networkDevice?.name,false)
            binding.etNetworkDeviceTwo.setSelection(currentPlanIndex)
            selectedNetworkDeviceTwo = networkDevices[currentPlanIndex]
        }*//*

    }
*/

    private fun setUpHostNetworkDeviceSpinners(networkDevice: List<NetworkDevice>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevice)

        binding.etHostDevice.setAdapter(adapter)
        binding.etHostDevice.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedHostNetworkDevice = networkDevice[pos]
            }
        if (networkDevice.isNotEmpty()) {
            val currentPlanIndex = networkDevice.indexOf(viewModel.subscription?.hostDevice)
            binding.etHostDevice.setText(viewModel.subscription?.hostDevice?.name, false)
            binding.etHostDevice.setSelection(currentPlanIndex)
            selectedHostNetworkDevice = networkDevice[currentPlanIndex]
        }
    }

    private fun setUpPlaceSpinner(places: List<Place>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, places)
        binding.etPlace.setAdapter(adapter)
        binding.etPlace.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                selectedPlace = places[pos]
            }
        if (places.isNotEmpty()) {
            val currentPlanIndex = places.indexOf(viewModel.subscription?.place)
            binding.etPlace.setText(viewModel.subscription?.place?.name, false)
            binding.etPlace.setSelection(currentPlanIndex)
            selectedPlace = places[currentPlanIndex]
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
        if (napBoxes.isNotEmpty()) {
            val currentPlanIndex = napBoxes.indexOf(viewModel.subscription?.napBox)
            binding.etNapBox.setText(viewModel.subscription?.napBox?.code, false)
            binding.etNapBox.setSelection(currentPlanIndex)
            selectedNapBox = napBoxes[currentPlanIndex]
        }
    }
}
