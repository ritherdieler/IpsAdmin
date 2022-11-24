package com.dscorp.ispadmin.presentation.plan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.login.LoginViewModel
import com.dscorp.ispadmin.presentation.registration.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.concurrent.Flow

@AndroidEntryPoint
class PlanFragment : Fragment() {
    lateinit var etNamePlan: EditText
    lateinit var etPrecio: EditText
    lateinit var etDownloadSpeed: EditText
    lateinit var etUploadSpeed: EditText
    lateinit var btRegisterPlan:Button
    val viewModel: PlanViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscribirse()
        val view = inflater.inflate(R.layout.fragment_plan, container, false)
        etNamePlan = view.findViewById(R.id.etNamePlan)
        etPrecio = view.findViewById(R.id.etPrecio)
        etDownloadSpeed = view.findViewById(R.id.etDownloadSpeed)
        etUploadSpeed = view.findViewById(R.id.etUploadSpeed)
        btRegisterPlan= view.findViewById(R.id.btRegisterPlan)

        btRegisterPlan.setOnClickListener{
            registerPlan()
        }
        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    fun subscribirse() {
        viewModel.planLiveData.observe(viewLifecycleOwner) {
            showSucessDialog(it.name)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showErrorDialog(it.message.toString())
        }
    }

    fun showSucessDialog(plan: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Plan Registrado con Exito")
        builder.setMessage(plan)
        builder.setPositiveButton("Ok") { p0, p1 ->


        }
        builder.show()
    }

    fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("El plan no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    fun registerPlan() {
        var namePlan = etNamePlan.text.toString()
        var precio = etPrecio.text.toString()
        var downloadSpeed = etDownloadSpeed.text.toString()
        var uploadSpeed = etUploadSpeed.text.toString()

        viewModel.validateForm(
            namePlan = namePlan,
            precio = precio,
            downloadSpeed = downloadSpeed,
            uploadSpeed = uploadSpeed,
            etNamePlan = etNamePlan,
            etPrecio = etPrecio,
            etDownloadSpeed = etDownloadSpeed,
            etUploadSpeed = etUploadSpeed
        )
    }

    fun navigateToRegister() {

        println("Click")

        var intent = Intent(requireActivity(), RegisterActivity::class.java)
        startActivity(intent)
    }
}