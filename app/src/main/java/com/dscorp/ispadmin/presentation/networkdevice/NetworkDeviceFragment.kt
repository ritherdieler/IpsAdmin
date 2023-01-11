package com.dscorp.ispadmin.presentation.networkdevice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.whenCreated
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNetworkDeviceBinding
import com.dscorp.ispadmin.presentation.networkdevice.NetworkDeviceFormError.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NetworkDeviceFragment : Fragment() {
    lateinit var binding:FragmentNetworkDeviceBinding
    val viewModel: NetworkDeviceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*
     binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_network_device,null,true)
*/
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_network_device, null, true)

        binding.btRegisterNetworkDevice.setOnClickListener{

            registerNetworkDevice()
            observeNetWorkDeviceResponse()
            observeNetworkDeviceFormError()
        }

        return binding.root

    }
    private fun observeNetWorkDeviceResponse() {
         viewModel.networkDeviceResponseLiveData.observe(viewLifecycleOwner){response ->
             when(response){
                 is NetworkDeviceResponse.OnNetworkDeviceRegistered ->showSucessDialog(response.networkDevice.name)
                 is NetworkDeviceResponse.OnError -> showErrorLiveData(response.error.message.toString())
             }
         }
    }

    private fun observeNetworkDeviceFormError()
    {viewModel.networkDeviceFormErrorLiveData.observe(viewLifecycleOwner) { formError ->
        when (formError) {
            is OnEtAddress -> binding.etIpAddress.setError(formError.error)
            is OnEtNameError -> binding.etName.setError(formError.error)
            is OnEtPassword -> binding.etPassword.setError(formError.error)
            is OnEtUserName -> binding.etUsername.setError(formError.error)
        }
     }
    }

    fun showSucessDialog(networkDeviceName:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Dispositivo $networkDeviceName registrado correctamente.")
        builder.setMessage("")
        builder.setPositiveButton("ok"){p0,p1->
        }
        builder.show()
    }

    fun showErrorLiveData(error:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ERROR:el dispositivo de red no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("ok"){p0,p1->
        }
        builder.show()
    }
    fun registerNetworkDevice(){
        with(binding) {
            var name: String= etName.text.toString()
            var password: String =etPassword.text.toString()
            var username: String = etUsername.text.toString()
            var ipaddress: String = etIpAddress.text.toString()

            viewModel.validateForm(
                etName,
                etPassword,
                etUsername,
                etIpAddress,
                name,
                password,
                username,
                ipaddress
            )
        }
    }
}