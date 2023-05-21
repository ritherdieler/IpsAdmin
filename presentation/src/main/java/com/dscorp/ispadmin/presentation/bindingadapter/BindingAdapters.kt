package com.dscorp.ispadmin.presentation.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

//visibility from boolean binding adapter
@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) TextView.VISIBLE else TextView.GONE
}


@BindingAdapter("app:imeActionListener")
fun setImeActionListener(view: TextInputEditText, listener: () -> Unit) {
    view.setOnEditorActionListener { _, _, _ ->
        listener()
        true
    }
}

@BindingAdapter("app:focusablem")
fun focusable(view: TextInputEditText, value: MutableLiveData<Boolean>) {

    val observer = Observer<Boolean> {
        view.isFocusable = it
        view.isFocusableInTouchMode = it
        view.isEnabled = it
    }

    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            view.findViewTreeLifecycleOwner()?.let { value.observe(it, observer) }
        }

        override fun onViewDetachedFromWindow(v: View) {
            value.removeObserver(observer)
        }
    })
}


@BindingAdapter("app:icon")
fun changeFabIcon(fab:FloatingActionButton, value : MutableLiveData<Int>){

    val observer = Observer<Int> {
        fab.setImageResource(it)
    }

    fab.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            fab.findViewTreeLifecycleOwner()?.let { value.observe(it, observer) }
        }

        override fun onViewDetachedFromWindow(v: View) {
            value.removeObserver(observer)
        }
    })
}
