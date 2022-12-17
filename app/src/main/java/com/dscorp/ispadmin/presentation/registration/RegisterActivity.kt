package com.dscorp.ispadmin.presentation.registration

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding



    val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_register, null, true)
        setContentView(binding.root)


       binding.btRegister.setOnClickListener {
            register()

        }

        observe()


    }

    private fun observeRegisterResponse() {
        viewModel.registerResponseLiveData.observe(this){it ->
            when(it){
                is RegisterResponse.OnError ->showErrorDialog(it.error.message.toString())
                is RegisterResponse.OnRegister ->showSucessDialog(it.register.name.toString())
            }
        }
    }

    private fun ObserveRegisterFormError() {
        viewModel.registerFormErrorLiveData.observe(this) { it ->
        when(it){
            is RegisterFormError.OnEtFirstNameError -> binding.etFirstName.setError(it.error)
            is RegisterFormError.OnEtLastNameError -> binding.etLastName.setError(it.error)
            is RegisterFormError.OnEtPassword1Error -> binding.etPassword1.setError(it.error)
            is RegisterFormError.OnEtPassword2Error -> binding.etPassword2.setError(it.error)
            is RegisterFormError.OnEtUserError -> binding.etUser.setError(it.error)
        }
        }
    }

    fun observe() {
        ObserveRegisterFormError()
        observeRegisterResponse()
    }

    fun showSucessDialog(nombre: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Usuario Registrado con Exito")
        builder.setMessage(nombre)
        builder.setPositiveButton("Ok") { p0, p1 ->
            finish()
        }
        builder.show()
    }

    fun showErrorDialog(error: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("El usuario no fue registrado")
        builder.setMessage(error)
        builder.setPositiveButton("Ok") { p0, p1 ->
        }
        builder.show()
    }

    fun register() {
        var usertext = binding.etUser.text.toString()
        var passwordtext1 = binding.etPassword1.text.toString()
        var passwordtext2 = binding.etPassword2.text.toString()
        var firstnametext = binding.etFirstName.text.toString()
        var lastnametext = binding.etLastName.text.toString()

        viewModel.validateForm(
            user = usertext,
            password1 = passwordtext1,
            password2 = passwordtext2,
            firstName = firstnametext,
            lastName = lastnametext,
        )
    }
}
