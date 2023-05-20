package com.dscorp.ispadmin.presentation.di.modules

import com.dscorp.ispadmin.presentation.ui.features.forms.SubscriptionForm
import org.koin.dsl.module

val formFieldModule = module {
    // subscriptionFormField

    factory { SubscriptionForm() }


}