package com.dscorp.ispadmin.presentation.registration

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dscorp.ispadmin.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    lateinit var etUser: EditText
    lateinit var etPassword1: EditText
    lateinit var etPassword2: EditText
    lateinit var etFirstName: EditText
    lateinit var etLastName: EditText
    lateinit var btRegister: Button

    val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUser = findViewById(R.id.etUser)
        etPassword1 = findViewById(R.id.etPassword1)
        etPassword2 = findViewById(R.id.etPassword2)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        btRegister = findViewById(R.id.btRegister)

        btRegister.setOnClickListener {
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
        var usertext = etUser.text.toString()
        var passwordtext1 = etPassword1.text.toString()
        var passwordtext2 = etPassword2.text.toString()
        var firstnametext = etFirstName.text.toString()
        var lastnametext = etLastName.text.toString()


        viewModel.validateForm(
            user = usertext,
            password1 = passwordtext1,
            password2 = passwordtext2,
            firstName = firstnametext,
            lastName = lastnametext,
            etUser = etUser,
            etFirstName = etFirstName,
            etLastName = etLastName,
            etPassword1 = etPassword1,
            etPassword2 = etPassword2
        )


    }


}
