package com.dscorp.ispadmin.domain.model.subscription

fun subscriptionClientIpAddressError(value: String, required: Boolean): String? {
    val trimmed = value.trim()
    if (!required && trimmed.isEmpty()) return null
    if (trimmed.isEmpty()) return "Ingrese la IP del cliente"
    return if (IPV4_PATTERN.matches(trimmed)) null else "La IP del cliente no es válida"
}

private val IPV4_PATTERN = Regex(
    "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
)
