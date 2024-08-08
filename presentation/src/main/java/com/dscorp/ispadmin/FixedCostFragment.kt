package com.dscorp.ispadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.databinding.FragmentFixedCostBinding
import com.dscorp.ispadmin.presentation.ui.features.fixedCost.components.FixedCostScreen

class FixedCostFragment : Fragment() {
    val binding: FragmentFixedCostBinding by lazy { FragmentFixedCostBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.root.setContent { FixedCostScreen() }
        return binding.root
    }

}