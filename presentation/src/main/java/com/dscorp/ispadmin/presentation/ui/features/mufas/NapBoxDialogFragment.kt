package com.dscorp.ispadmin.presentation.ui.features.mufas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapboxDialogBinding
import com.example.cleanarchitecture.domain.domain.entity.NapBox

class NapBoxDialogFragment : DialogFragment(){

    private val args: NapBoxDialogFragmentArgs by navArgs()
    lateinit var binding: FragmentNapboxDialogBinding
    lateinit var mufa: NapBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mufa = args.napBox

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                layoutInflater, R.layout.fragment_napbox_dialog, null, true
            )
        binding.tvSeeNapBoxId.setText(args.napBox.id).toString()
        binding.tvSeeCode.setText(args.napBox.code).toString()
        binding.tvSeeAddress.setText(args.napBox.address).toString()

        binding.ivButtonClose.setOnClickListener {
        dismiss()
    }
        return binding.root
    }
}