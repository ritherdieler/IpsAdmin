package com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation

interface ReactiveFieldValidator<T> {
    fun validate(fieldValue: T):Boolean
}