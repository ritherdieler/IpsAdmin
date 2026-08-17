package com.dscorp.ispadmin.presentation.ui.features.subscription.register.models

data class VlanOption(
    val value: String,
    val label: String
) {
    override fun toString(): String = label
}

val VLAN_OPTIONS = listOf(
    VlanOption(value = "1", label = "VLAN 1"),
    VlanOption(value = "100", label = "VLAN 100")
)
