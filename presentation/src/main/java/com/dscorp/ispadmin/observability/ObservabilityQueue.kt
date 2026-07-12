package com.dscorp.ispadmin.observability

import android.content.Context
import java.io.File

class ObservabilityQueue(
    context: Context,
    fileName: String = "events-queue.jsonl"
) {

    private val directory = File(context.filesDir, "observability").apply { mkdirs() }
    private val queueFile = File(directory, fileName)
    private val lock = Any()
    private val maxQueuedEvents = 500

    fun append(json: String) = synchronized(lock) {
        queueFile.appendText(json + "\n")
        trimToLimit()
    }

    fun readAll(): List<String> = synchronized(lock) {
        if (!queueFile.exists()) emptyList()
        else queueFile.readLines().filter { it.isNotBlank() }
    }

    fun removeFirst(count: Int) = synchronized(lock) {
        if (count <= 0 || !queueFile.exists()) return
        val remaining = queueFile.readLines().filter { it.isNotBlank() }.drop(count)
        writeAll(remaining)
    }

    private fun writeAll(lines: List<String>) {
        if (lines.isEmpty()) {
            queueFile.delete()
            return
        }
        queueFile.writeText(lines.joinToString(separator = "\n", postfix = "\n"))
    }

    private fun trimToLimit() {
        if (!queueFile.exists()) return
        val lines = queueFile.readLines().filter { it.isNotBlank() }
        if (lines.size > maxQueuedEvents) {
            writeAll(lines.takeLast(maxQueuedEvents))
        }
    }
}
