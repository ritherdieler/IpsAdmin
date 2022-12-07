package com.dscorp.ispadmin.presentation.Subscription

import android.content.Intent
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
import com.dscorp.ispadmin.presentation.registration.RegisterActivity
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

        observe()

        binding.btSubscribirse.setOnClickListener {
            registerSubscription()
        }
        setOnSpnPlanItemSelectedListener()
        setOnSpnDeviceItemSelectedListener()
        return binding.root
    }

    private fun registerSubscription() {
        val firstname = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val dni = binding.etDni.text.toString()
        val password = binding.etPassword.text.toString()
        val address = binding.etAddress.text.toString()
        val phoneNumber = binding.etPhone.text.toString()
        val subscriptionDate = binding.etSubscriptionDate.text.toString().toInt()
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

    fun showErrorLiveData(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("La subscripcion no fue procesada")
        builder.setMessage(error)
        builder.setPositiveButton("ok") { p0, p1 ->
        }
        builder.show()
    }


    private fun observe() {
        observeToSubscriptionLiveData()
        observeToErrorLiveData()
        observePlanListLiveData()
        observeNetworkDevice()
    }

    private fun observeNetworkDevice() {
        viewModel.deviceListLiveData.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            binding.spnDevice.adapter = adapter
        }
    }

    private fun observePlanListLiveData() {
        viewModel.planListLiveData.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            binding.spnPlan.adapter = adapter
        }
    }

    private fun observeToErrorLiveData() {
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showErrorLiveData(it.message.toString())
        }
    }

    private fun observeToSubscriptionLiveData() {
        viewModel.subscriptionLiveData.observe(viewLifecycleOwner) {
            showSucessDialog(it.firstName)
        }
    }


    fun navigateToRegister() {

        println("Click")

        var intent = Intent(requireActivity(), RegisterActivity::class.java)
        startActivity(intent)
    }

    fun doSubscription() {
        with(binding) {
            val firstName: String = etFirstName.text.toString()
            val lastName: String = etLastName.text.toString()
            val password: String = etPassword.text.toString()
            val dni: String = etDni.text.toString()
            val phone: String = etPhone.text.toString()
            val address: String = etAddress.text.toString()
            val subscriptionDate: Int = etSubscriptionDate.toString().toInt()


            viewModel.registerSubscription(
                firstname = firstName,
                lastname = lastName,
                password = password,
                dni = dni,
                address = address,
                phone = phone,
                subscriptionDate = subscriptionDate,
                planId = selectedPlan?.id?:"",
                networkDeviceId = selectedNetworkDevice?.id?:"",


                )
        }

    }

}