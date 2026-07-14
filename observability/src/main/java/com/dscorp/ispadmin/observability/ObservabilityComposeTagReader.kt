package com.dscorp.ispadmin.observability

object ObservabilityComposeTagReader {

    fun tagFromSemanticsName(name: String?, value: Any?): String? {
        if (name != "TestTag" && name != "SentryTag") return null
        return (value as? String)?.takeIf { it.isNotBlank() }
    }

    fun isClickActionName(name: String?): Boolean =
        name == "OnClick"

    fun isScrollActionName(name: String?): Boolean =
        name == "ScrollBy"

    fun isEditableActionName(name: String?): Boolean =
        name == "EditableText" || name == "SetText" || name == "RequestFocus"

    fun isToggleActionName(name: String?): Boolean =
        name == "ToggleableState" || name == "OnToggle"

    fun isInteractiveActionName(name: String?): Boolean =
        isClickActionName(name) || isEditableActionName(name) || isToggleActionName(name)

    fun isClickableModifierClass(className: String?): Boolean =
        className == "androidx.compose.foundation.ClickableElement" ||
            className == "androidx.compose.foundation.CombinedClickableElement"

    fun isToggleableModifierClass(className: String?): Boolean =
        className == "androidx.compose.foundation.selection.ToggleableElement" ||
            className == "androidx.compose.foundation.selection.TriStateToggleableElement"

    fun isScrollableModifierClass(className: String?): Boolean =
        className == "androidx.compose.foundation.ScrollingLayoutElement"

    fun isTestTagElementClass(className: String?): Boolean =
        className == "androidx.compose.ui.platform.TestTagElement"

    fun isInteractiveModifierClass(className: String?): Boolean =
        isClickableModifierClass(className) || isToggleableModifierClass(className)
}
