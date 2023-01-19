package com.dscorp.ispadmin.presentation.serviceorden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentServiceOrderBinding
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class ServiceOrderFragment : Fragment() {
    lateinit var binding: FragmentServiceOrderBinding
    var selectedDate: Long = 0
    var selectedAttentionDate: Long = 0
    val viewModel: ServiceOrderViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_service_order, null, true)
        observeServiceOrderResponse()
        observeServiceFormError()

        binding.btRegisterServiceOrder.setOnClickListener {
            registerServiceOrder()
        }
        binding.etCreateDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
            datePicker
            datePicker.addOnPositiveButtonClickListener {
                selectedDate = it
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val formattedDate = formatter.format(it)
                binding.etCreateDate.setText(formattedDate)
            }
            datePicker.show(childFragmentManager, "DatePicker")
        }
        binding.etAttentionDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
            datePicker
            datePicker.addOnPositiveButtonClickListener {
                selectedAttentionDate = it
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val formattedDate = formatter.format(it)
                binding.etAttentionDate.setText(formattedDate)
            }

            datePicker.show(childFragmentManager, "DatePicker")
        }

        return binding.root
    }

    private fun observeServiceOrderResponse() {
        viewModel.serviceOrderResponseLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ServiceOrderResponse.OnError -> showErrorDialog(response.error.message.toString())
                is ServiceOrderResponse.OnServiceOrderRegistered -> showSucessDialog(response.serviceOrder.createDate)
            }
        }
    }

    private fun observeServiceFormError() {
        viewModel.serviceOrderFormErrorLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ServiceOrderFormError.OnEtLatitudeError -> binding.etLatitude.error =
                    response.error
                is ServiceOrderFormError.OnEtLogintudeError -> binding.etLongitude.error =
                    response.error
                is ServiceOrderFormError.OnEtCreateDateError -> binding.etCreateDate.error =
                    response.error
                is ServiceOrderFormError.OnEtAttentionDate -> binding.etAttentionDate.error =
                    response.error
            }
        }
    }

    private fun registerServiceOrder() {
        val serviceOrder = ServiceOrder(
            latitude = binding.etLatitude.text.toString().toDouble(),
            longitude = binding.etLongitude.text.toString().toDouble(),
            createDate = selectedDate,
            attentionDate = selectedAttentionDate

        )

        viewModel.registerServiceOrder(serviceOrder)
    }

    fun showSucessDialog(service_orden: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Orden de registro registrado con Exito")
        builder.setMessage("")
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El service_orden no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }
}