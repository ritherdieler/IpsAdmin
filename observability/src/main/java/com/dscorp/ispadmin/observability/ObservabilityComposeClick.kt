package com.dscorp.ispadmin.observability

object ObservabilityComposeClick {

    @Volatile
    private var uiCapture: ObservabilityUiCapture? = null

    fun bind(capture: ObservabilityUiCapture) {
        uiCapture = capture
    }

    fun report(tag: String, label: String? = null) {
        val payload = LinkedHashMap<String, Any?>()
        payload["source"] = "compose"
        payload["tag"] = tag
        if (!label.isNullOrBlank()) payload["label"] = label
        uiCapture?.capture(
            type = ObsUiEventType.CLICK,
            target = tag,
            value = null,
            data = payload
        )
    }
}
