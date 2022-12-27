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
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceOrderFragment : Fragment() {
    lateinit var binding : FragmentServiceOrderBinding
    val viewModel: ServiceOrderViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_service_order,null,true)
        observeServiceOrderResponse()
        observeServiceFormError()

        binding.btRegisterServiceOrder.setOnClickListener{
            registerServiceOrder()
        }
        return binding.root
    }

    private fun observeServiceOrderResponse() { viewModel.serviceOrderResponseLiveData.observe(viewLifecycleOwner){
            response ->
            when(response){
                is ServiceOrderResponse.OnError ->showErrorDialog(response.error.message.toString())
                is ServiceOrderResponse.OnServiceOrderRegistered ->showSucessDialog(response.serviceOrder.createDate)
            }
        }
    }
    private fun observeServiceFormError() { viewModel.ServiceOrderFormLiveData.observe(viewLifecycleOwner){
            response ->
            when(response){
                is ServiceOrderFormError.OnEtLatitudError->binding.etLatitud.error=response.error
                is ServiceOrderFormError.OnEtLogintudError ->binding.etLongitud.error=response.error
                is ServiceOrderFormError.OnEtCreateDateError ->binding.etCreateDate.error=response.error
                is ServiceOrderFormError.OnEtAttentionDate ->binding.etAttentionDate.error=response.error

            }
        }
    }

    private fun registerServiceOrder() {
        viewModel.registerServiceOrder(
            binding.etLongitud.text.toString().toDouble(),
            binding.etLatitud.text.toString().toDouble(),
            binding.etCreateDate.text.toString().toInt(),
            binding.etAttentionDate.text.toString().toInt(),
        )
    }

    fun showSucessDialog(service_orden: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Orden de registro registrado con Exito")
        builder.setMessage(service_orden)
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