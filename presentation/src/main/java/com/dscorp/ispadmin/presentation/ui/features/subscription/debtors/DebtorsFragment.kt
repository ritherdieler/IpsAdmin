package com.dscorp.ispadmin.presentation.ui.features.subscription.debtors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dscorp.ispadmin.R
import org.koin.android.ext.android.inject

class DebtorsFragment : Fragment() {

    private  val viewModel: DebtorsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debtors, container, false)
    }


}