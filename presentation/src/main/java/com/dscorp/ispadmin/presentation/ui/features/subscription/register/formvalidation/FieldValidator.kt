package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

interface FieldValidator<T> {
    fun checkIfFieldIsValid(fieldValue: T?):Boolean
}