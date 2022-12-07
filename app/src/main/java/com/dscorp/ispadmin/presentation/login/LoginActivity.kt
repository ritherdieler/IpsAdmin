package com.dscorp.ispadmin.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.main.MainActivity
import com.dscorp.ispadmin.presentation.registration.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, true)
        setContentView(binding.root)

        subscribirse()

        binding.btLogin.setOnClickListener {
            doLogin()
        }

        binding.tvCreateAccount.setOnClickListener {
            navigateToRegister()
        }
    }

    fun subscribirse() {

        viewModel.loginLiveData.observe(this) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        viewModel.errorLiveData.observe(this) {
            Toast.makeText(this, "Ocurrio un error, verifique que sus credenciales sean validas", Toast.LENGTH_SHORT)
                .show()
            println(it.message)
        }


    }


    fun doLogin() {

        var usertext = binding.etUser.text.toString()
        var passwordtext = binding.etPassword.text.toString()


        viewModel.validateForm(
            usertext,
            passwordtext,
            binding.etUser,
            binding.etPassword
        )

    }

    fun navigateToRegister() {

        println("Click")

        var intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }


}