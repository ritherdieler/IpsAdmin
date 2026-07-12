package com.dscorp.ispadmin.observability

import com.google.gson.Gson
import java.util.UUID

object ObservabilityEventSender {

    suspend fun flush(
        api: ObservabilityApi,
        queue: ObservabilityQueue,
        gson: Gson,
        apiKey: String,
        batchSize: Int = 100
    ): Boolean {
        if (apiKey.isBlank()) return true
        while (true) {
            val lines = queue.readAll()
            if (lines.isEmpty()) return true
            val consumed = lines.take(batchSize)
            val events = consumed.mapNotNull {
                runCatching { gson.fromJson(it, ObsEventDto::class.java) }.getOrNull()
            }
            if (events.isEmpty()) {
                queue.removeFirst(consumed.size)
                continue
            }
            val correlationId = UUID.randomUUID().toString()
            val response = runCatching {
                api.sendEvents(apiKey, correlationId, ObsBatchRequest(events))
            }.getOrNull() ?: return false
            if (response.isSuccessful) {
                queue.removeFirst(consumed.size)
                if (consumed.size >= lines.size) return true
            } else {
                return false
            }
        }
    }
}
