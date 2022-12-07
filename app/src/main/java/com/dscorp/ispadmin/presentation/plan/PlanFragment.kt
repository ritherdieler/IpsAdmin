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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityRegisterBinding
import com.dscorp.ispadmin.databinding.FragmentPlanBinding
import com.dscorp.ispadmin.presentation.login.LoginViewModel
import com.dscorp.ispadmin.presentation.registration.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.concurrent.Flow

@AndroidEntryPoint
class PlanFragment : Fragment() {
    lateinit var binding : FragmentPlanBinding
    val viewModel: PlanViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscribirse()
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_plan,null,true
        )

        binding.btRegisterPlan.setOnClickListener{
            registerPlan()
        }
        return binding.root


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
        var namePlan = binding.etNamePlan.text.toString()
        var precio = binding.etPrecio.text.toString()
        var downloadSpeed = binding.etDownloadSpeed.text.toString()
        var uploadSpeed = binding.etUploadSpeed.text.toString()

        viewModel.validateForm(
            namePlan = namePlan,
            precio = precio,
            downloadSpeed = downloadSpeed,
            uploadSpeed = uploadSpeed,
            etNamePlan = binding.etNamePlan,
            etPrecio = binding.etPrecio,
            etDownloadSpeed = binding.etDownloadSpeed,
            etUploadSpeed = binding.etUploadSpeed
        )
    }

    fun navigateToRegister() {

        println("Click")

        var intent = Intent(requireActivity(), RegisterActivity::class.java)
        startActivity(intent)
    }
}