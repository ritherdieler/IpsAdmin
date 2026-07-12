package com.dscorp.ispadmin.di

import com.dscorp.ispadmin.BuildConfig
import com.dscorp.ispadmin.observability.ObservabilityActivityTracker
import com.dscorp.ispadmin.observability.ObservabilityApi
import com.dscorp.ispadmin.observability.ObservabilityClient
import com.dscorp.ispadmin.observability.ObservabilityConnectivityMonitor
import com.dscorp.ispadmin.observability.ObservabilityContextProvider
import com.dscorp.ispadmin.observability.ObservabilityHttpInterceptor
import com.dscorp.ispadmin.observability.ObservabilityQueue
import com.dscorp.ispadmin.observability.ObservabilityReplayConfig
import com.dscorp.ispadmin.observability.ObservabilityReplaySender
import com.dscorp.ispadmin.observability.ObservabilityScreenRecorder
import com.dscorp.ispadmin.observability.ObservabilitySpanApi
import com.dscorp.ispadmin.observability.ObservabilityTracer
import com.dscorp.ispadmin.observability.ObservabilityWorkScheduler
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val OBS_BASE_URL = "OBS_BASE_URL"

val observabilityQualifier = named("observability")
val observabilitySpansQueueQualifier = named("observability-spans-queue")

val observabilityModule = module {

    single(observabilityQualifier) { Gson() }

    single(observabilityQualifier) { provideObservabilityHttpClient(get(observabilityQualifier)) }

    single(observabilityQualifier) {
        provideObservabilityRetrofit(
            url = getProperty(OBS_BASE_URL),
            okHttpClient = get(observabilityQualifier),
            gson = get(observabilityQualifier)
        )
    }

    single { get<Retrofit>(observabilityQualifier).create(ObservabilityApi::class.java) }

    single { get<Retrofit>(observabilityQualifier).create(ObservabilitySpanApi::class.java) }

    single { ObservabilityContextProvider(get()) }

    single { ObservabilityQueue(androidContext()) }

    single(observabilitySpansQueueQualifier) {
        ObservabilityQueue(androidContext(), "spans-queue.jsonl")
    }

    single { ObservabilityWorkScheduler(androidContext()) }

    single { ObservabilityReplayConfig() }

    single { ObservabilityScreenRecorder(config = get()) }

    single {
        ObservabilityReplaySender(
            api = get(),
            recorder = get(),
            gson = get(observabilityQualifier),
            apiKey = BuildConfig.OBS_API_KEY,
            config = get()
        )
    }

    single { ObservabilityActivityTracker(recorder = get()) }

    single {
        ObservabilityClient(
            api = get(),
            queue = get(),
            contextProvider = get(),
            gson = get(observabilityQualifier),
            apiKey = BuildConfig.OBS_API_KEY,
            workScheduler = get(),
            replaySender = get()
        )
    }

    single {
        ObservabilityTracer(
            api = get(),
            queue = get(observabilitySpansQueueQualifier),
            contextProvider = get(),
            gson = get(observabilityQualifier),
            apiKey = BuildConfig.OBS_API_KEY
        )
    }

    single {
        ObservabilityHttpInterceptor(
            clientProvider = lazy { get<ObservabilityClient>() },
            tracerProvider = lazy { get<ObservabilityTracer>() }
        )
    }

    single {
        ObservabilityConnectivityMonitor(
            context = androidContext(),
            clientProvider = lazy { get<ObservabilityClient>() },
            tracerProvider = lazy { get<ObservabilityTracer>() }
        )
    }
}

private fun provideObservabilityHttpClient(gson: Gson): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
    if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        builder.addInterceptor(logging)
    }
    return builder.build()
}

private fun provideObservabilityRetrofit(url: String, okHttpClient: OkHttpClient, gson: Gson): Retrofit =
    Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
