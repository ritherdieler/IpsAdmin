package com.dscorp.ispadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dscorp.ispadmin.databinding.FragmentCrossDialogBinding

class CrossDialogFragment(
    val text: String? = null,
    val lottieResource: Int? = null,
    val positiveButtonText: String? = null,
    val onCloseButtonClick: (() -> Unit)? = null,
    val onPositiveButtonClick: (() -> Unit)? = null,
) :
    DialogFragment() {

    private lateinit var binding: FragmentCrossDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrossDialogBinding.inflate(inflater, container, false)
        text?.let {
            binding.dialogText.text = text
        }

        lottieResource?.let {
            binding.lottieAnimation.setAnimation(lottieResource)
        }

        binding.acceptButton.setOnClickListener {
            if (onPositiveButtonClick == null) dismiss()
            else onPositiveButtonClick.invoke()
        }

        positiveButtonText?.let {
            binding.acceptButton.text = it
        }

        binding.closeButton.setOnClickListener {
            if (onCloseButtonClick == null) dismiss()
            else onCloseButtonClick.invoke()
        }

        return binding.root
    }

}