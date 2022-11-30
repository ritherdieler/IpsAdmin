package com.dscorp.ispadmin.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.main.MainActivity
import com.dscorp.ispadmin.presentation.registration.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var etUser: EditText
    lateinit var etPassword: EditText
    lateinit var btLogin: Button
    lateinit var tvCreateAccount: TextView

    val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        subscribirse()

        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        btLogin = findViewById(R.id.btLogin)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)

        btLogin.setOnClickListener {
            doLogin()
        }

        tvCreateAccount.setOnClickListener{
            navigateToRegister()
        }

    }



    fun subscribirse() {

        viewModel.loginLiveData.observe(this) {
           var intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
        }

        viewModel.errorLiveData.observe(this) {
            Toast.makeText(this, "Ocurrio un error, verifique que sus credenciales sean validas", Toast.LENGTH_SHORT)
                .show()
            println(it.message)
        }


    }


    fun doLogin() {

        var usertext = etUser.text.toString()
        var passwordtext = etPassword.text.toString()


        viewModel.validateForm(
            usertext,
            passwordtext,
            etUser,
            etPassword
        )

    }

    fun navigateToRegister() {

        println("Click")

        var intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }




}