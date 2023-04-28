package com.dscorp.ispadmin.presentation.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField

//visibility from boolean binding adapter
@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) TextView.VISIBLE else TextView.GONE
}

