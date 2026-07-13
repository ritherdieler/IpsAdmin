package com.dscorp.ispadmin.observability

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException

object ObsBreadcrumbCategory {
    const val NAVIGATION = "navigation"
    const val USER_ACTION = "user_action"
    const val NETWORK = "network"
    const val STATE = "state"
}

fun obsTags(
    feature: String,
    screen: String,
    action: String,
    entityId: Any? = null,
    extra: Map<String, Any?> = emptyMap()
): Map<String, Any?> {
    val tags = LinkedHashMap<String, Any?>()
    tags["feature"] = feature
    tags["screen"] = screen
    tags["action"] = action
    if (entityId != null) tags["entityId"] = entityId
    tags.putAll(extra)
    return tags
}

fun ViewModel.trackScreen(
    client: ObservabilityClient,
    feature: String,
    screen: String,
    data: Map<String, Any?> = emptyMap()
) {
    client.addBreadcrumb(
        category = ObsBreadcrumbCategory.NAVIGATION,
        message = "$feature.screen_view",
        data = data + mapOf("feature" to feature, "screen" to screen)
    )
}

fun ViewModel.trackUserAction(
    client: ObservabilityClient,
    feature: String,
    screen: String,
    action: String,
    data: Map<String, Any?> = emptyMap()
) {
    client.addBreadcrumb(
        category = ObsBreadcrumbCategory.USER_ACTION,
        message = "$feature.$action",
        data = data + mapOf("feature" to feature, "screen" to screen, "action" to action)
    )
}

suspend fun <T> ViewModel.runTracked(
    client: ObservabilityClient,
    feature: String,
    screen: String,
    action: String,
    entityId: Any? = null,
    extra: Map<String, Any?> = emptyMap(),
    errorMessage: String? = null,
    severity: String = "error",
    block: suspend () -> T
): Result<T> {
    client.addBreadcrumb(
        category = ObsBreadcrumbCategory.USER_ACTION,
        message = "$feature.$action",
        data = obsTags(feature, screen, action, entityId, extra)
    )
    return try {
        val result = block()
        client.addBreadcrumb(
            category = ObsBreadcrumbCategory.STATE,
            message = "$feature.$action.success",
            data = obsTags(feature, screen, action, entityId)
        )
        Result.success(result)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        client.reportError(
            throwable = throwable,
            message = errorMessage ?: "Error en $feature.$action",
            severity = severity,
            tags = obsTags(feature, screen, action, entityId, extra)
        )
        Result.failure(throwable)
    }
}
