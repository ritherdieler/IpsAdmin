package com.dscorp.ispadmin.observability

import org.koin.core.qualifier.named

object ObservabilityQualifiers {
    val gson = named("observability-gson")
    val httpClient = named("observability-http")
    val retrofit = named("observability-retrofit")
    val spansQueue = named("observability-spans-queue")
}
