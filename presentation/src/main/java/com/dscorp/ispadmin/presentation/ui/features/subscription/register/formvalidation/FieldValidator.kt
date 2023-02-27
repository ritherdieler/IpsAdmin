package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

interface FieldValidator {
    fun checkIfFieldIsValid(fieldValue: String?):Boolean
}