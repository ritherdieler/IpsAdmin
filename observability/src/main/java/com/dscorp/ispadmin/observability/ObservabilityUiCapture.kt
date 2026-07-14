package com.dscorp.ispadmin.observability

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

object ObsUiEventType {
    const val CLICK = "click"
    const val FOCUS = "focus"
    const val TEXT_CHANGE = "text_change"
    const val CHECKBOX = "checkbox"
    const val SWITCH = "switch"
    const val LONG_CLICK = "long_click"
}

class ObservabilityUiCapture(
    private val clientProvider: Lazy<ObservabilityClient>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    private val textChangeDebounceMs: Long = DEFAULT_TEXT_CHANGE_DEBOUNCE_MS,
    private val clickDedupeMs: Long = DEFAULT_CLICK_DEDUPE_MS,
    private val clock: () -> Long = { System.currentTimeMillis() }
) {

    private data class PendingTextChange(
        val target: String?,
        val value: String?,
        val data: Map<String, Any?>
    )

    private val pendingByTarget = ConcurrentHashMap<String, PendingTextChange>()
    private val jobsByTarget = ConcurrentHashMap<String, Job>()
    @Volatile private var lastClickTarget: String? = null
    @Volatile private var lastClickAtMs: Long = 0L

    fun capture(
        type: String,
        target: String? = null,
        value: String? = null,
        data: Map<String, Any?> = emptyMap()
    ) {
        if (type == ObsUiEventType.TEXT_CHANGE) {
            scheduleTextChange(target, value, data)
            return
        }
        flushPendingTextChanges()
        if (type == ObsUiEventType.CLICK && shouldDedupeClick(target)) {
            return
        }
        emit(type, target, value, data)
    }

    private fun shouldDedupeClick(target: String?): Boolean {
        val key = target?.takeIf { it.isNotBlank() } ?: return false
        val now = clock()
        if (key == lastClickTarget && now - lastClickAtMs < clickDedupeMs) {
            return true
        }
        lastClickTarget = key
        lastClickAtMs = now
        return false
    }

    fun flushPendingTextChanges() {
        jobsByTarget.values.forEach { it.cancel() }
        jobsByTarget.clear()
        val pending = pendingByTarget.values.toList()
        pendingByTarget.clear()
        pending.forEach { item ->
            emit(ObsUiEventType.TEXT_CHANGE, item.target, item.value, item.data)
        }
    }

    private fun scheduleTextChange(
        target: String?,
        value: String?,
        data: Map<String, Any?>
    ) {
        val key = target?.takeIf { it.isNotBlank() } ?: "_"
        jobsByTarget.remove(key)?.cancel()
        pendingByTarget[key] = PendingTextChange(target, value, data)
        jobsByTarget[key] = scope.launch {
            delay(textChangeDebounceMs)
            val pending = pendingByTarget.remove(key) ?: return@launch
            jobsByTarget.remove(key)
            emit(ObsUiEventType.TEXT_CHANGE, pending.target, pending.value, pending.data)
        }
    }

    private fun emit(
        type: String,
        target: String?,
        value: String?,
        data: Map<String, Any?>
    ) {
        val payload = LinkedHashMap<String, Any?>()
        payload["type"] = type
        if (!target.isNullOrBlank()) payload["target"] = target
        if (value != null) payload["value"] = value
        payload.putAll(data)
        clientProvider.value.addBreadcrumb(
            category = ObsBreadcrumbCategory.UI,
            message = type,
            data = payload
        )
    }

    companion object {
        const val DEFAULT_TEXT_CHANGE_DEBOUNCE_MS = 2000L
        const val DEFAULT_CLICK_DEDUPE_MS = 400L
    }
}
