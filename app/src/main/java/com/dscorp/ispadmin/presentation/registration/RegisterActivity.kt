package com.dscorp.ispadmin.presentation.registration

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityRegisterBinding
import com.dscorp.ispadmin.databinding.FragmentSubscriptionBinding
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

        suscribirse()

    }

    fun suscribirse() {

        //Aqui el observador se subscribe al observable
        viewModel.userLiveData.observe(this) {

            showSucessDialog(it.name)
        }

        viewModel.errorLiveData.observe(this) {

            showErrorDialog(it.message!!)
        }


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
            etUser = binding.etUser,
            etFirstName = binding.etFirstName,
            etLastName = binding.etLastName,
            etPassword1 = binding.etPassword1,
            etPassword2 = binding.etPassword2
        )


    }


}
