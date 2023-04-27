package com.dscorp.components

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import com.dscorp.components.base.BaseDialogFragment
import com.dscorp.components.databinding.FragmentProgressFullScreenDialogBinding

class ProgressFullScreenDialogFragment(
    val title: String? = null,
    val description: String? = null,
    @DrawableRes val drawable: Int? = null
) : BaseDialogFragment() {

    private val binding by lazy { FragmentProgressFullScreenDialogBinding.inflate(layoutInflater) }

    private var progressAnimator: ObjectAnimator? = null

    override fun getTheme() = R.style.FullScreenDialogStyle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        title?.let {
            binding.tvTitle.text = title
        }
        description?.let {
            binding.tvDescription.text = description
        }

        drawable?.let {
            binding.ivLoader.setImageResource(drawable)
        }

        isCancelable = false
        val progressAnimator =
            ObjectAnimator.ofFloat(binding.ivLoader, "rotation", 0f, 360f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
        progressAnimator.start()

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        progressAnimator?.cancel()
        super.onDismiss(dialog)
    }

}