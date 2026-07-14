package com.dscorp.ispadmin.observability

import android.view.accessibility.AccessibilityEvent

data class ObsTextChangePayload(
    val target: String,
    val value: String
)

object ObservabilityAccessibilityText {

    fun fromEvent(
        eventType: Int,
        text: List<CharSequence>?,
        contentDescription: CharSequence?,
        className: CharSequence?
    ): ObsTextChangePayload? {
        if (eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return null
        val value = text?.joinToString(separator = "")?.takeIf { it.isNotEmpty() } ?: return null
        val target = contentDescription?.toString()?.takeIf { it.isNotBlank() }
            ?: className?.toString()?.substringAfterLast('.')?.takeIf { it.isNotBlank() }
            ?: "compose_text"
        return ObsTextChangePayload(target = target, value = value)
    }
}
