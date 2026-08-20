package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

fun registrationProgressStepMessage(elapsedMs: Long): String = when {
    elapsedMs < 15_000L -> "Registrando…"
    elapsedMs < 40_000L -> "Autorizando ONU…"
    elapsedMs < 90_000L -> "Esperando ACS…"
    else -> "Aplicando WiFi…"
}
