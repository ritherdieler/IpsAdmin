package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.databinding.FragmentDashBoardBinding

class DashBoardFragment : Fragment() {
    val binding by lazy { FragmentDashBoardBinding.inflate(layoutInflater) }

    private val viewModel: DashBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }


}