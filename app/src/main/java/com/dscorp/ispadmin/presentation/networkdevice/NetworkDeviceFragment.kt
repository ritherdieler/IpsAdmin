package com.dscorp.ispadmin.presentation.networkdevice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NetworkDeviceFragment : Fragment() {
    lateinit var etName: EditText
    lateinit var etPassword: EditText
    lateinit var etUsername: EditText
    lateinit var etIpAddress: EditText
    lateinit var btRegisterNetworkDevice: Button
    val viewModel: NetworkDeviceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscribirse()
        val view = inflater.inflate(R.layout.fragment_network_device, container, false)
        etName = view.findViewById(R.id.etFirstName)
        etPassword = view.findViewById(R.id.ltPassword)
        etUsername = view.findViewById(R.id.etUsername)
        etIpAddress = view.findViewById(R.id.etIpAddress)
        btRegisterNetworkDevice = view.findViewById(R.id.btRegisterNetworkDevice)

        btRegisterNetworkDevice.setOnClickListener{


          registerNetworkDevice()
        }

        return view

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    fun subscribirse(){
        viewModel.networkDeviceLiveData.observe(viewLifecycleOwner){
            showSucessDialog(it.name)
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner){
            showErrorLiveData(it.message.toString())
        }
    }

    fun showSucessDialog(networkDeviceName:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Dispositivo de red registrado correctamente ")
        builder.setMessage(networkDeviceName)
        builder.setPositiveButton("ok"){p0,p1->
        }
        builder.show()
    }

    fun showErrorLiveData(error:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setMessage(error)
        builder.setPositiveButton("ok"){p0,p1->
        }
        builder.show()
    }
    fun registerNetworkDevice(){
        var name: String = etName.text.toString()
        var password:String = etPassword.text.toString()
        var username:String = etUsername.text.toString()
        var ipaddress:String = etIpAddress.text.toString()

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