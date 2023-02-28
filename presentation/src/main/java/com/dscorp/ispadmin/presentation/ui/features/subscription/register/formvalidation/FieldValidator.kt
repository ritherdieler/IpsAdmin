package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

interface FieldValidator<T> {
    fun validate(fieldValue: T?):Boolean
}