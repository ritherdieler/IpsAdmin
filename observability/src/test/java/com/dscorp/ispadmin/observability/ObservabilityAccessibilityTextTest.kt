package com.dscorp.ispadmin.observability

import android.view.accessibility.AccessibilityEvent
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObservabilityAccessibilityTextTest {

    @Test
    fun `fromEvent ignora tipos que no son text changed`() {
        val result = ObservabilityAccessibilityText.fromEvent(
            eventType = AccessibilityEvent.TYPE_VIEW_CLICKED,
            text = listOf("x"),
            contentDescription = "field",
            className = "android.widget.EditText"
        )
        assertThat(result).isNull()
    }

    @Test
    fun `fromEvent captura text_change de Compose`() {
        val result = ObservabilityAccessibilityText.fromEvent(
            eventType = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
            text = listOf("secreto"),
            contentDescription = "password",
            className = "androidx.compose.ui.platform.AndroidComposeView"
        )
        assertThat(result).isEqualTo(
            ObsTextChangePayload(target = "password", value = "secreto")
        )
    }

    @Test
    fun `fromEvent usa className cuando no hay contentDescription`() {
        val result = ObservabilityAccessibilityText.fromEvent(
            eventType = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
            text = listOf("abc"),
            contentDescription = null,
            className = "androidx.compose.ui.viewinterop.AndroidViewsHandler"
        )
        assertThat(result?.target).isEqualTo("AndroidViewsHandler")
        assertThat(result?.value).isEqualTo("abc")
    }
}
