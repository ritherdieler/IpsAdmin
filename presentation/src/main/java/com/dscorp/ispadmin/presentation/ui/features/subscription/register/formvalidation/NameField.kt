package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.R
import org.koin.java.KoinJavaComponent.inject

class NameField() {

    private val applicationContext: Context by inject(Context::class.java)

    var firstName: String? = null

    val hint: String = applicationContext.getString(R.string.names)

    val errorLiveData = MutableLiveData<String?>(null)

    var isValid: Boolean = false
    get() {
        errorLiveData.value = applicationContext.getString(R.string.invalidFirstNameField)
        return field
    }

    private fun validateField(fieldValue: String?): Boolean {
        return if (fieldValue?.trim().isNullOrEmpty()) {
            errorLiveData.value = applicationContext.getString(R.string.invalidFirstNameField)
            firstName = fieldValue
            isValid = false
            false
        } else {
            errorLiveData.value = null
            firstName = null
            isValid = true
            true
        }
    }

    fun onTextChanged(fieldValue: CharSequence, start: Int, before: Int, count: Int) {
        validateField(fieldValue.toString())
    }

}

