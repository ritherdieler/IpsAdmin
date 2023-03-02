package com.dscorp.ispadmin.presentation.ui.features.mufas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentMufaDialogBinding
import com.example.cleanarchitecture.domain.domain.entity.Mufa
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse

class MufaDialogFragment : DialogFragment(){

    private val args: MufaDialogFragmentArgs by navArgs()
    lateinit var binding: FragmentMufaDialogBinding
    lateinit var mufa: Mufa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mufa = args.mufas

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                layoutInflater, R.layout.fragment_mufa_dialog, null, true
            )
        binding.mufa = mufa
        binding.executePendingBindings()
/*
        binding.tvSeeReference.text = mufa.reference
*/
   /*     binding.tvSeeMufaId.text = mufa.id.toString()*/
        binding.ivButtonClose.setOnClickListener {
        dismiss()
    }
        return binding.root
    }
}