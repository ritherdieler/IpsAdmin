package com.dscorp.ispadmin.presentation.ui.features.subscription.register.models

enum class RegisterSubscriptionWizardStep {
    CLIENT_LOCATION,
    INSTALLATION,
    CONFIRMATION,
    ;

    fun next(): RegisterSubscriptionWizardStep? = when (this) {
        CLIENT_LOCATION -> INSTALLATION
        INSTALLATION -> CONFIRMATION
        CONFIRMATION -> null
    }

    fun previous(): RegisterSubscriptionWizardStep? = when (this) {
        CLIENT_LOCATION -> null
        INSTALLATION -> CLIENT_LOCATION
        CONFIRMATION -> INSTALLATION
    }
}

fun wizardFieldsFor(
    step: RegisterSubscriptionWizardStep,
    form: RegisterSubscriptionFormState,
): List<FormFieldKey> = when (step) {
    RegisterSubscriptionWizardStep.CLIENT_LOCATION -> listOf(
        FormFieldKey.FIRST_NAME,
        FormFieldKey.LAST_NAME,
        FormFieldKey.DNI,
        FormFieldKey.PHONE,
        FormFieldKey.ADDRESS,
        FormFieldKey.PLACE,
        FormFieldKey.LOCATION,
    )
    RegisterSubscriptionWizardStep.INSTALLATION -> buildList {
        add(FormFieldKey.PLAN)
        add(FormFieldKey.HOST_DEVICE)
        if (form.requiresNapBox()) add(FormFieldKey.NAP_BOX)
        if (form.requiresOnu()) add(FormFieldKey.ONU)
        if (form.requiresWifiConfig()) {
            add(FormFieldKey.WIFI_SSID_24)
            add(FormFieldKey.WIFI_PASSWORD_24)
            if (form.useDifferentWifiNames) add(FormFieldKey.WIFI_SSID_5)
        }
        if (form.requiresClientIpAddress) add(FormFieldKey.CLIENT_IP_ADDRESS)
    }
    RegisterSubscriptionWizardStep.CONFIRMATION -> listOf(
        FormFieldKey.FACADE_PHOTO,
        FormFieldKey.NOTE,
    )
}

fun canAdvanceWizardStep(
    step: RegisterSubscriptionWizardStep,
    form: RegisterSubscriptionFormState,
): Boolean = wizardFieldsFor(step, form).all { form.validate(it) == null }
