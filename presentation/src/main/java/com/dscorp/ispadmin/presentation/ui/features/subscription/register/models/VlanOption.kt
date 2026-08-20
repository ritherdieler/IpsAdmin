package com.dscorp.ispadmin.presentation.ui.features.subscription.register.models

const val DEFAULT_REGISTRATION_VLAN = "100"

data class VlanOption(
    val value: String,
    val label: String,
    val selectable: Boolean = true,
) {
    override fun toString(): String = label
}

val VLAN_OPTIONS = listOf(
    VlanOption(value = "1", label = "VLAN 1", selectable = false),
    VlanOption(value = "100", label = "VLAN 100", selectable = true),
)

fun isRegistrationVlanSelectable(vlan: String): Boolean =
    VLAN_OPTIONS.any { it.value == vlan && it.selectable }
