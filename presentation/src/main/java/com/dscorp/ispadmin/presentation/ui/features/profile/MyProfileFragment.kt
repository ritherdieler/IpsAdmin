package com.dscorp.ispadmin.presentation.ui.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentMyProfileBinding
import com.dscorp.ispadmin.presentation.ui.features.login.LoginActivity
import com.example.cleanarchitecture.domain.domain.entity.User
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyProfileFragment : Fragment() {
    lateinit var binding: FragmentMyProfileBinding
    val viewModel: MyProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my_profile, null, true)

        binding.btnLogOut.setOnClickListener { logOut() }

        observeResponse()

        return binding.root
    }
    private fun logOut() {
        viewModel.logOut()
        activity?.startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    private fun observeResponse() {
        viewModel.myProfileLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is MyProfileResponse.UserSessionFound -> showProfileData(response.user)
                is MyProfileResponse.OnError -> {}
            }
        }
    }

    private fun showProfileData(user: User) {
        binding.user = user
        binding.executePendingBindings()
    }
}
