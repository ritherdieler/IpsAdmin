package com.dscorp.ispadmin.observability

object ObservabilityComposeText {

    @Volatile
    private var uiCapture: ObservabilityUiCapture? = null

    fun bind(capture: ObservabilityUiCapture) {
        uiCapture = capture
    }

    fun report(
        tag: String,
        value: String,
        label: String? = null
    ) {
        val payload = LinkedHashMap<String, Any?>()
        payload["source"] = "compose"
        payload["tag"] = tag
        if (!label.isNullOrBlank()) payload["label"] = label
        uiCapture?.capture(
            type = ObsUiEventType.TEXT_CHANGE,
            target = tag,
            value = value,
            data = payload
        )
    }
}
