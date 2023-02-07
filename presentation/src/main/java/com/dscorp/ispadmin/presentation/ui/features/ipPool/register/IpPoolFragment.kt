package com.dscorp.ispadmin.presentation.ui.features.ipPool.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentIpPoolBinding
import org.koin.android.ext.android.inject

class IpPoolFragment : Fragment() {

    val binding by lazy { FragmentIpPoolBinding.inflate(layoutInflater) }

    private val viewModel: IpPoolViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        return binding.root
    }

}