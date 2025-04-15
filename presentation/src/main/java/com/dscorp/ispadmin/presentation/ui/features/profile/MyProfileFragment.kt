package com.dscorp.ispadmin.presentation.ui.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.presentation.theme.MyTheme
import com.dscorp.ispadmin.presentation.ui.features.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyProfileFragment : Fragment() {
    private val viewModel: MyProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {
                    MyProfileScreen(
                        viewModel = viewModel,
                        onLogout = {
                            navigateToLogin()
                        }
                    )
                }
            }
        }
    }
    
    private fun navigateToLogin() {
        activity?.let {
            startActivity(Intent(it, LoginActivity::class.java))
            it.finish()
        }
    }
}
