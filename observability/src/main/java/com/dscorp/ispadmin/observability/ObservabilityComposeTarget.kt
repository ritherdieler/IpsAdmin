package com.dscorp.ispadmin.observability

object ObservabilityComposeTarget {

    private val GENERIC_HOSTS = setOf(
        "AndroidComposeView",
        "AndroidViewsHandler",
        "ComposeView",
        "View",
        "unknown"
    )

    fun isComposeHost(className: String?): Boolean {
        if (className.isNullOrBlank()) return false
        return className.contains("AndroidComposeView") ||
            className.contains("AndroidViewsHandler") ||
            className.contains("ComposeView")
    }

    fun isGenericHost(label: String?): Boolean {
        if (label.isNullOrBlank()) return true
        return label in GENERIC_HOSTS
    }

    fun fromClassName(className: String?): String? {
        val simple = className?.substringAfterLast('.')?.takeIf { it.isNotBlank() } ?: return null
        return when {
            simple.contains("Button", ignoreCase = true) -> "Button"
            simple.contains("CheckBox", ignoreCase = true) -> "Checkbox"
            simple.contains("Switch", ignoreCase = true) -> "Switch"
            simple.contains("RadioButton", ignoreCase = true) -> "RadioButton"
            simple.contains("Image", ignoreCase = true) -> "Image"
            simple.contains("EditText", ignoreCase = true) -> "TextField"
            isGenericHost(simple) -> null
            else -> simple
        }
    }

    fun resolve(
        testTag: String? = null,
        contentDescription: String? = null,
        text: String? = null,
        role: String? = null,
        editable: Boolean = false,
        className: String? = null
    ): String {
        testTag?.takeIf { it.isNotBlank() }?.let { return it }
        contentDescription?.takeIf { it.isNotBlank() }?.let { return it }
        if (!editable) {
            text?.takeIf { it.isNotBlank() }?.let { return it }
        }
        role?.takeIf { it.isNotBlank() }?.let { return it }
        fromClassName(className)?.let { return it }
        return "compose_unlabeled"
    }
}
