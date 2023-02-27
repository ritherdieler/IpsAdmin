package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.koin.java.KoinJavaComponent.inject

class FormField(
    private val hintResourceId: Int,
    private val errorResourceId: Int,
    private val fieldValidator: FieldValidator
) {

    private val applicationContext: Context by inject(Context::class.java)

    var value: String? = null

    val hint: String = applicationContext.getString(hintResourceId)

    val errorLiveData = MutableLiveData<String?>(null)

    var isValid: Boolean = false
        get() {
            if (!field) errorLiveData.value = applicationContext.getString(errorResourceId)
            return field
        }

     fun validateField(fieldValue: String?): Boolean {
        return if (!fieldValidator.checkIfFieldIsValid(fieldValue)) {
            errorLiveData.value = applicationContext.getString(errorResourceId)
            isValid = false
            false
        } else {
            value = fieldValue
            errorLiveData.value = null
            isValid = true
            true
        }
    }

    fun onTextChanged(fieldValue: CharSequence, start: Int, before: Int, count: Int) {
        validateField(fieldValue.toString())
    }

}

