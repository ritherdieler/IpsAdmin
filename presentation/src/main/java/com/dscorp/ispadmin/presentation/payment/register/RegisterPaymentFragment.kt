package com.dscorp.ispadmin.presentation.payment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.databinding.FragmentRegisterPaymentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterPaymentFragment : Fragment() {

    private val viewModel: RegisterPaymentViewModel by viewModel()
    private val binding by lazy { FragmentRegisterPaymentBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }


}