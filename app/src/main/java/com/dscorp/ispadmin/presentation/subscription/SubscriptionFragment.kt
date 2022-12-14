package com.dscorp.ispadmin.presentation.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentSubscriptionBinding
import com.dscorp.ispadmin.presentation.subscription.SubscriptionFormError.*
import com.dscorp.ispadmin.presentation.subscription.SubscriptionResponse.*
import com.dscorp.ispadmin.repository.model.NetworkDevice
import com.dscorp.ispadmin.repository.model.Plan
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionFragment : Fragment() {

    lateinit var binding: FragmentSubscriptionBinding
    var selectedPlan: Plan? = null
    var selectedNetworkDevice: NetworkDevice? = null
    val viewModel: SubscriptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_subscription, null, true)

        observeResponse()
        observeFormError()

        binding.btSubscribirse.setOnClickListener {
            registerSubscription()
        }
        setOnSpnPlanItemSelectedListener()
        setOnSpnDeviceItemSelectedListener()
        return binding.root
    }

    private fun observeResponse() {
        viewModel.responseLiveData.observe(viewLifecycleOwner){ response ->
            when (response) {
                is OnError ->showErrorDialog(response.error.message.toString())
                is OnPlansFetched -> setUpPlansSpinner(response.plans)
                is OnSubscriptionRegistered ->showSucessDialog(response.subscription.firstName)
                is OnNetworkDeviceFetched -> setUpNetworkDeviceSpinner(response.networkdevices)
            }
        }
    }

    private fun observeFormError() {
        viewModel.formErrorLiveData.observe(viewLifecycleOwner){formError ->
            when (formError) {
                is OnEtAddressesError -> setEtAddressError(formError)
                is OnEtDniError -> setEtDniError(formError)
                is OnEtFirstNameError -> setEtFirstNameError(formError)
                is OnEtLastNameError -> setEtLastNameError(formError)
                is OnEtNumberPhoneError -> setEtNumberPhoneError(formError)
                is OnEtPasswordError -> setEtPasswordError(formError)
                is OnEtSubscriptionDateError -> setSubscriptionDateError(formError)
                is OnSpnNetworkDeviceError ->{}
                is OnSpnPlanError -> {}
            }
        }
    }

    private fun setSubscriptionDateError(formError: OnEtSubscriptionDateError) {
        binding.etSubscriptionDate.setError(formError.error)
    }

    private fun setEtPasswordError(formError: OnEtPasswordError) {
        binding.etPassword.setError(formError.error)
    }

    private fun setEtNumberPhoneError(formError: OnEtNumberPhoneError) {
        binding.etPhone.setError(formError.error)
    }

    private fun setEtLastNameError(formError: OnEtLastNameError) {
        binding.etLastName.setError(formError.error)
    }

    private fun setEtFirstNameError(formError: OnEtFirstNameError) {
        binding.etFirstName.setError(formError.error)
    }

    private fun setEtDniError(formError: OnEtDniError) {
        binding.etDni.setError(formError.error)
    }

    private fun setEtAddressError(formError: OnEtAddressesError) {
        binding.etAddress.setError(formError.error)
    }

    private fun registerSubscription() {
        val firstname = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val dni = binding.etDni.text.toString()
        val password = binding.etPassword.text.toString()
        val address = binding.etAddress.text.toString()
        val phoneNumber = binding.etPhone.text.toString()
        val subscriptionDate = binding.etSubscriptionDate.text.toString()
        val planId = selectedPlan?.id ?: ""
        val networkDevice = selectedNetworkDevice?.id ?: ""

        viewModel.registerSubscription(
            firstname, lastName, password, dni, address, phoneNumber, subscriptionDate, planId, networkDevice
        )
    }

    private fun setOnSpnDeviceItemSelectedListener() {
        binding.spnDevice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected: NetworkDevice = p0?.selectedItem as NetworkDevice
                Toast.makeText(requireContext(), selected.name, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setOnSpnPlanItemSelectedListener() {
        binding.spnPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected: Plan = p0?.selectedItem as Plan
                Toast.makeText(requireContext(), selected.name, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    fun showSucessDialog(subscriptionFirstName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Te Subscribiste con exito ")
        builder.setMessage(subscriptionFirstName)
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }

    fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("La subscripcion no fue procesada")
        builder.setMessage(error)
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }

    private fun setUpPlansSpinner(plans: List<Plan>) {
        val adapter = ArrayAdapter(
            requireContext(), android.R
                .layout.simple_spinner_item, plans
        )
        binding.spnPlan.adapter = adapter
    }

    private fun setUpNetworkDeviceSpinner(networkDevices: List<NetworkDevice>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, networkDevices)
        binding.spnDevice.adapter = adapter
    }
}
