package com.dscorp.ispadmin.presentation.ui.features.payment.payerFinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.databinding.FragmentFindPayerBinding
import com.dscorp.ispadmin.presentation.ui.features.payment.payerFinder.compose.PayerFinderScreen

class FindPayerFragment : Fragment() {

    val binding: FragmentFindPayerBinding by lazy { FragmentFindPayerBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.root.setContent {
            PayerFinderScreen()
        }

        return binding.root
    }

}