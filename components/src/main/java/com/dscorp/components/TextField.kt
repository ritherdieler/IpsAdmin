package com.dscorp.components

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dscorp.components.databinding.ViewEditTextBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TextField @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

    private val binding = ViewEditTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var textChangeListener: ((String) -> Unit)? = null
        set(value) {
            field = value
            binding.editText.addTextChangedListener {
                value?.invoke(it.toString())
            }
        }

    var inputText: String = ""
        set(value) {
            field = value
            binding.editText.setText(value)
        }

    var hint: String? = null
        set(value) {
            field = value
            binding.editText.hint = value
        }

    var error: String? = null
        set(value) {
            field = value
            binding.editText.error = value
        }

    var inputType: Int = InputType.TYPE_CLASS_TEXT
        set(value) {
            field = value
            binding.editText.inputType = value
        }

    var imeActionListener: (() -> Unit)? = null
        set(value) {
            field = value
            binding.editText.setOnEditorActionListener { _, _, _ ->
                value?.invoke()
                true
            }
        }

    var textFilters: Array<InputFilter>? = null
        set(value) {
            field = value
            binding.editText.filters = value
        }

    init {
        loadAttributes()
    }

    private fun loadAttributes() {
        val attributes = context.obtainStyledAttributes(R.styleable.EditText)
        inputText = attributes.getString(R.styleable.EditText_inputText)?:""
        hint = attributes.getString(R.styleable.EditText_hint)
        error = attributes.getString(R.styleable.EditText_error)
        inputType = attributes.getInt(R.styleable.EditText_inputType, InputType.TYPE_CLASS_TEXT)
        attributes.recycle()
    }

    @BindingAdapter("app:imeActionListener")
    fun setImeActionListener(textField: TextField, listener: (() -> Unit)?) {
        textField.imeActionListener = listener
    }

    @BindingAdapter("app:textFilters")
    fun setTextFilters(textField: TextField, filters: Array<InputFilter>?) {
        textField.textFilters = filters
    }


    @BindingAdapter("app:inputText")
    fun setText(view: TextField, text: String?) {
        view.inputText = text?: ""
    }

    @InverseBindingAdapter(attribute = "app:inputText", event = "textAttrChanged")
    fun getText(view: TextField): String {
        return view.inputText
    }

    @BindingAdapter("app:textAttrChanged")
    fun setListener(view: TextField, listener: InverseBindingListener?) {
        if (listener != null) {
            view.textChangeListener = {
                listener.onChange()
            }
        } else {
            view.textChangeListener = null
        }
    }

    @BindingAdapter("app:inputText")
    fun setLiveDataText(textField: TextField, livedata: MutableLiveData<String?>) {

        textField.binding.editText.text

        val observer = Observer<String?> { value ->
            textField.inputText = value?:""
        }
        livedata.observe(context as LifecycleOwner, observer)

        // Cuando ya no necesites escuchar los cambios del Flow, llama a coroutineScope.cancel() para cancelar la tarea

        textField.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View) {}

            override fun onViewDetachedFromWindow(p0: View) {
                livedata.removeObserver(observer)
            }
        })
    }

    @BindingAdapter("app:inputText")
    fun setFlowText(textField: TextField, flow: MutableStateFlow<String?>) {
        val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
        coroutineScope.launch {
            flow.collect { data ->
                this@TextField.inputText = data?:""
            }
        }
        // Cuando ya no necesites escuchar los cambios del Flow, llama a coroutineScope.cancel() para cancelar la tarea
        textField.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View) {}

            override fun onViewDetachedFromWindow(p0: View) {
                coroutineScope.cancel()
            }
        })

    }

}