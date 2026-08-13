package com.dscorp.ispadmin.presentation.ui.features.subscription.register.models

enum class FormFieldKey {
    FIRST_NAME,
    LAST_NAME,
    DNI,
    ADDRESS,
    PHONE,
    PLAN,
    PLACE,
    ONU,
    NAP_BOX,
    NOTE,
    FACADE_PHOTO,
    HOST_DEVICE,
    EQUIPMENT_CONDITION,
    CLIENT_IP_ADDRESS;

    companion object {
        val blockingForSubmit: List<FormFieldKey> =
            entries.filter { it != EQUIPMENT_CONDITION && it != CLIENT_IP_ADDRESS }
    }
}
