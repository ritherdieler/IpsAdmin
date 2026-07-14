package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObservabilityComposeTagReaderTest {

    @Test
    fun `reconoce TestTag y SentryTag`() {
        assertThat(ObservabilityComposeTagReader.tagFromSemanticsName("TestTag", "login_submit"))
            .isEqualTo("login_submit")
        assertThat(ObservabilityComposeTagReader.tagFromSemanticsName("SentryTag", "btn"))
            .isEqualTo("btn")
        assertThat(ObservabilityComposeTagReader.tagFromSemanticsName("Text", "x")).isNull()
    }

    @Test
    fun `reconoce modifiers clickables de Compose 1_5+`() {
        assertThat(
            ObservabilityComposeTagReader.isClickableModifierClass(
                "androidx.compose.foundation.ClickableElement"
            )
        ).isTrue()
        assertThat(
            ObservabilityComposeTagReader.isTestTagElementClass(
                "androidx.compose.ui.platform.TestTagElement"
            )
        ).isTrue()
        assertThat(ObservabilityComposeTagReader.isClickableModifierClass("android.widget.Button"))
            .isFalse()
    }

    @Test
    fun `reconoce acciones y modifiers interactivos de campos y checkbox`() {
        assertThat(ObservabilityComposeTagReader.isEditableActionName("SetText")).isTrue()
        assertThat(ObservabilityComposeTagReader.isEditableActionName("EditableText")).isTrue()
        assertThat(ObservabilityComposeTagReader.isToggleActionName("ToggleableState")).isTrue()
        assertThat(
            ObservabilityComposeTagReader.isToggleableModifierClass(
                "androidx.compose.foundation.selection.ToggleableElement"
            )
        ).isTrue()
        assertThat(ObservabilityComposeTagReader.isInteractiveActionName("OnClick")).isTrue()
        assertThat(ObservabilityComposeTagReader.isInteractiveActionName("SetText")).isTrue()
        assertThat(ObservabilityComposeTagReader.isInteractiveActionName("Text")).isFalse()
    }

    @Test
    fun `elige tag de nodo interactivo mas profundo`() {
        val tag = ObservabilityComposeTargetLocatorLogic.resolveTargetTag(
            listOf(
                NodeProbe(tags = listOf("screen"), interactive = false),
                NodeProbe(tags = listOf("login_username"), interactive = true),
                NodeProbe(tags = listOf("login_username_inner"), interactive = false)
            )
        )
        assertThat(tag).isEqualTo("login_username")
    }

    @Test
    fun `si no hay interactivo usa el testTag mas profundo bajo el punto`() {
        val tag = ObservabilityComposeTargetLocatorLogic.resolveTargetTag(
            listOf(
                NodeProbe(tags = listOf("screen"), interactive = false),
                NodeProbe(tags = listOf("login_password"), interactive = false)
            )
        )
        assertThat(tag).isEqualTo("login_password")
    }
}

data class NodeProbe(
    val tags: List<String>,
    val interactive: Boolean
)

object ObservabilityComposeTargetLocatorLogic {
    fun resolveTargetTag(nodesContainingPointInDepthOrder: List<NodeProbe>): String? {
        var lastKnownTag: String? = null
        var targetTag: String? = null
        var deepestTag: String? = null
        for (node in nodesContainingPointInDepthOrder) {
            if (node.tags.isNotEmpty()) {
                lastKnownTag = node.tags.last()
                deepestTag = lastKnownTag
            }
            if (node.interactive) {
                targetTag = lastKnownTag
            }
        }
        return targetTag ?: deepestTag
    }
}
