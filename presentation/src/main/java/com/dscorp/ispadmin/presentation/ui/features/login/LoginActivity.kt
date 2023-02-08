package com.dscorp.ispadmin.presentation.ui.features.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.ActivityLoginBinding
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivity
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, true)
        setContentView(binding.root)
/*
        val sp = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
*/

/*
        checkLogin(sp)
*/

        observe()
        binding.btLogin.setOnClickListener {
            doLogin()
/*
            rememberUser(sp)
*/

        }

        binding.tvCreateAccount.setOnClickListener {
            navigateToRegister()
        }
    }

  /*  private fun checkLogin(sp: SharedPreferences) {
        if (sp.getString("active", "") == "true") {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            if (sp.getString("remember", "") == "true") {
                binding.ltUser.editText?.setText(sp.getString("user", ""))
                binding.ltPassword.editText?.setText(sp.getString("password", ""))
            }
        }
    }

    private fun rememberUser(sharedpreference: SharedPreferences) {
        val user = binding.ltUser.editText?.text.toString()
        val password = binding.ltPassword.editText?.text.toString()

        if (user.isNotEmpty() && password.isNotEmpty()) {
            val checkBox = binding.checkBox
            if (checkBox.isChecked) {
                with(sharedpreference.edit()) {
                    putString("user", user)
                    putString("password", password)
                    putString("active", "true")
                    putString("remember", "true")
                    apply()
                }
            } else {
                with(sharedpreference.edit()) {
                    putString("active", "true")
                    putString("remember", "false")
                    apply()
                }
            }

        } else {
            Toast.makeText(this, "Intente Nuevamente", Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun observeLoginFormError() {
        viewModel.loginFormErrorLiveData.observe(this) { formError ->
            when (formError) {
                is LoginFormError.OnEtPassword -> binding.etPassword.setError(formError.error)
                is LoginFormError.OnEtUser -> binding.etUser.setError(formError.error)
            }
        }
    }

    private fun observeLoginResponse() {
        viewModel.loginResponseLiveData.observe(this) { response ->
            when (response) {
                is LoginResponse.OnError -> showErrorDialog(response.error.message)
                is LoginResponse.OnLoginSucess -> navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showErrorDialog(message: String?) {
        AlertDialog.Builder(this)
            .setTitle(R.string.login_Error_Tittle)
            .setMessage(message)
            .create()
            .show()
    }

    fun observe() {

        observeLoginResponse()
        observeLoginFormError()
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