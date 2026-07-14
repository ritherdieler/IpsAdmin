package com.dscorp.ispadmin.observability

interface ObservabilityEventStore {
    fun append(json: String)
    fun readAll(): List<String>
    fun removeFirst(count: Int)
}
