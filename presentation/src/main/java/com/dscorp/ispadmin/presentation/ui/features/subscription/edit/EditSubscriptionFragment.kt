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
    private var selectedTechnician: Technician? = null
    private var selectedNapBox: NapBox? = null
    private val viewModel: SubscriptionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.subscription = args.subscription
        viewModel.getFormData()
        observeResponse()
        observeFormError()

        binding.btSubscribirse.setOnClickListener {
            firebaseAnalytics.sendTouchButtonEvent(AnalyticsConstants.REGISTER_SUBSCRIPTION)
            editSubscription()
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
        setUpNetworkDeviceSpinners(response.networkDevices)
        setUpPlaceSpinner(response.places)
        setUpNapBoxSpinner(response.napBoxes)
        setUpHostNetworkDeviceSpinners(response.hostNetworkDevices)
    }

    private fun observeFormError() {
        viewModel.editFormErrorLiveData.observe(viewLifecycleOwner) { formError ->
            when (formError) {
                is OnEtFirstNameIsInvalidErrorRegisterUiState -> binding.tlFirstName.error =formError.error
                is OnEtLastNameIsInvalidErrorRegisterUiState -> binding.tlFirstName.error =formError.error
                is OnEtAddressesErrorRegisterUiState -> setEtAddressError(formError)
                is OnEtNumberPhoneErrorRegisterUiState -> setEtNumberPhoneError(formError)
                is OnEtPasswordErrorRegisterUiState -> setEtPasswordError(formError)
                is OnSpnNetworkDeviceErrorRegisterUiState -> setSpnNetworkDeviceError(formError)
                is OnSpnPlanErrorRegisterUiState -> setSpnPlanError(formError)
                is OnSpnPlaceErrorRegisterUiState -> setSpnPlaceError(formError)
                is OnEtLocationErrorRegisterUiState -> setEtLocationError(formError)
                is OnSpnNapBoxErrorRegisterUiState -> setSpnNapBoxError(formError)
                is OnPhoneIsInvalidErrorRegisterUiState -> binding.tlPhone.error = formError.error
                is OnPasswordIsInvalidErrorRegisterUiState -> binding.tlPassword.error =
                    formError.error
                is HostUiStateDeviceErrorRegister -> binding.spnHostDevice.error = formError.error

                //CLEAN FORM
                is CleanEtPasswordHasNotErrors -> binding.tlPassword.error = null
                is CleanEtAddressHasNotErrors -> binding.tlAddress.error = null
                is CleanEtPhoneHasNotErrors -> binding.tlPhone.error = null
                is CleanEtPlanNotErrors -> binding.spnPlan.error = null
                is CleanEtNetworkDeviceNotErrors -> binding.spnNetworkDeviceOne.error = null
                is CleanEtNapBoxNotErrors -> binding.spnNapBox.error = null
                is CleanEtPlaceNotErrors -> binding.spnPlace.error = null
                CleanEtFirstNameHasNotErrors -> binding.etFirstName.error = null
                CleanEtLastNameHasNotErrors -> binding.etLastName.error = null

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

    private fun setEtNumberPhoneError(formError: OnEtNumberPhoneErrorRegisterUiState) {
        binding.tlPhone.error = formError.error
    }


    private fun setEtAddressError(formError: OnEtAddressesErrorRegisterUiState) {
        binding.tlAddress.error = formError.error
    }

    private fun editSubscription() {

        val installedDevices = mutableListOf<Int>()
        selectedNetworkDeviceOne?.let { it.id?.let { it1 -> installedDevices.add(it1) } }
        selectedNetworkDeviceTwo?.let { it.id?.let { it1 -> installedDevices.add(it1) } }

        val subscription = Subscription(

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
            hostDeviceId = selectedHostNetworkDevice?.id ?: 0,
            id = null,
            code = null,
            firstName = "",
            lastName = "",
            dni = "",
            isNew = null,
            serviceIsSuspended = null,
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

        binding.etNetworkDeviceOne.setAdapter(adapter)
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
