package com.dscorp.ispadmin.presentation.ui.features.mufas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentMufaDialogBinding

class MufaDialogFragment : DialogFragment(){
    lateinit var binding: FragmentMufaDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                layoutInflater, R.layout.fragment_mufa_dialog, null, true
            )
        binding.ivButtonClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}