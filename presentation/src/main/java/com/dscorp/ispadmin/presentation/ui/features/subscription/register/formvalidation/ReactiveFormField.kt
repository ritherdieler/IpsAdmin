package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.koin.java.KoinJavaComponent.inject

class ReactiveFormField<T>(
    private val hintResourceId: Int? = null,
    private val errorResourceId: Int? = null,
    private val validator: ((validation: T?) -> Boolean)? = null,
) {

    private val applicationContext: Context by inject(Context::class.java)

    var liveData = CustomLiveData<T>(null, onValueChanged = { validateField(it) })

    fun getValue() = liveData.value
    val hint: String? = hintResourceId?.let { applicationContext.getString(it) }

    val errorLiveData = MutableLiveData<String?>(null)

    //Use this only for  enable or disable databinding views
    val isValidLiveData = MutableLiveData(false)

    fun isValid(): Boolean {
        val isValid = currentValueIsValid()
        if (!isValid) errorLiveData.value =
            errorResourceId?.let { applicationContext.getString(it) }
        return isValid
    }

    private fun currentValueIsValid() = validator?.let { it(liveData.value) } ?: true

    private fun validateField(fieldValue: T?): Boolean {
        return if (currentValueIsValid()) {
            errorLiveData.value = null
            isValidLiveData.value = true
            true
        } else {
            errorLiveData.value = errorResourceId?.let { applicationContext.getString(it) }
            isValidLiveData.value = false
            false
        }
    }
}

