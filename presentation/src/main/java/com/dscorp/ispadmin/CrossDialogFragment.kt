package com.dscorp.ispadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dscorp.ispadmin.databinding.FragmentCrossDialogBinding


class CrossDialogFragment(
    val text: String? = null,
    val onPositiveButtonClick: (() -> Unit)? = null
) :
    DialogFragment() {

    val binding by lazy { FragmentCrossDialogBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        text?.let {
            binding.dialogText.text = text
        }


        binding.acceptButton.setOnClickListener {
            if (onPositiveButtonClick == null) dismiss()
            else onPositiveButtonClick.invoke()
        }


        binding.closeButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}