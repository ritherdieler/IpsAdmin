package com.dscorp.ispadmin.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.dscorp.ispadmin.databinding.FragmentProgressButtonBinding

class ProgressButtonFragment @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: FragmentProgressButtonBinding = FragmentProgressButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var clickButtonProgress: (() -> Unit)? = null

    init {
        binding.button.setOnClickListener {
            clickButtonProgress?.invoke()
        }
    }

    fun setProgressBarVisible(progressBar: Boolean) {
        if (progressBar) {
            binding.progressBar.visibility = View.VISIBLE
            binding.button.textSize = 0f
        } else {
            binding.progressBar.visibility = View.GONE
            binding.button.textSize = 14f
        }
    }
    fun setProgressButtonDisable(status:Boolean){
        binding.button.isEnabled = status != true
        }
}
