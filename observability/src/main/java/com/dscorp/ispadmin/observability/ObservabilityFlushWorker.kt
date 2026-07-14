package com.dscorp.ispadmin.observability

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ObservabilityFlushWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val api: ObservabilityApi by inject()
    private val queue: ObservabilityQueue by inject()
    private val gson: Gson by inject(ObservabilityQualifiers.gson)
    private val config: ObservabilityConfig by inject()

    override suspend fun doWork(): Result {
        val delivered = runCatching {
            ObservabilityEventSender.flush(
                api = api,
                queue = queue,
                gson = gson,
                apiKey = config.apiKey
            )
        }.getOrDefault(false)
        return if (delivered) Result.success() else Result.retry()
    }
}
