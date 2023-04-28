package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.koin.java.KoinJavaComponent.inject

class ReactiveFormField<T>(
    private val hintResourceId: Int? = null,
    private val errorResourceId: Int? = null,
    private val validator: ((validation: T) -> Boolean)? = null,
) {

    private val applicationContext: Context by inject(Context::class.java)

    var liveData = CustomLiveData<T>(null, onValueChanged = { validateField(it) })

    fun getValue() = liveData.value
    val hint: String? = hintResourceId?.let { applicationContext.getString(it) }

    val errorLiveData = MutableLiveData<String?>(null)

    //Use this only for  enable or disable databinding views
    val isValidLiveData = MutableLiveData(false)

    var isValid: Boolean = false
        get() {
            if (!field) errorLiveData.value = errorResourceId?.let { applicationContext.getString(it) }
            return field
        }
    private fun validateField(fieldValue: T?): Boolean {
        return if (fieldValue == null || validator?.invoke(fieldValue) == false) {
            errorLiveData.value = errorResourceId?.let { applicationContext.getString(it) }
            isValid = false
            isValidLiveData.value = false
            false
        } else {
            errorLiveData.value = null
            isValid = true
            isValidLiveData.value = true
            true
        }
    }
}

