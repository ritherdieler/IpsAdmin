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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionFragment : Fragment() {

    lateinit var binding: FragmentSubscriptionBinding

    val viewModel: SubscriptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_subscription, null, true)

        observe()

        binding.btSubscribirse.setOnClickListener {
        }
        return binding.root
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
        with(binding){
            val firstName: String = etFirstName.text.toString()
            val lastName: String = etLastName.text.toString()
            val password: String = etPassword.text.toString()
            val dni: String = etDni.text.toString()
            val phone: String = etPhone.text.toString()
            val address: String = etAddress.text.toString()
            val subscriptionDate: Int = etSubscriptionDate.toString().toInt()


            viewModel.validateform(
                etFirstName = etFirstName,
                etLastName = etLastName,
                etPassword = etPassword,
                etDni = etDni,
                etAddress = etAddress,
                etPhone = etPhone,
                etSubscriptionDate = etSubscriptionDate,
                firstname = firstName,
                lastname = lastName,
                password = password,
                dni = dni,
                address = address,
                phone = phone,
                subscriptionDate = subscriptionDate,

                )
        }

    }

}