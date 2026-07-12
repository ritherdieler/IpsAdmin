package com.dscorp.ispadmin.observability

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dscorp.ispadmin.BuildConfig
import com.dscorp.ispadmin.di.observabilityQualifier
import com.google.gson.Gson
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ObservabilityFlushWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val api: ObservabilityApi by inject()
    private val queue: ObservabilityQueue by inject()
    private val gson: Gson by inject(observabilityQualifier)

    override suspend fun doWork(): Result {
        val delivered = runCatching {
            ObservabilityEventSender.flush(
                api = api,
                queue = queue,
                gson = gson,
                apiKey = BuildConfig.OBS_API_KEY
            )
        }.getOrDefault(false)
        return if (delivered) Result.success() else Result.retry()
    }
}
