package com.example.data2.data.di

import com.example.data2.BuildConfig
import com.example.data2.data.api.RestApiServices
import com.example.data2.data.api.SmartOltApiService
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single { providesApi(get()) }
    single {
        providesOltService(
            provideRetrofit(
                BuildConfig.OLT_SERVICE_BASE_URL,
                provideOltServiceHttpClient()
            )
        )
    }
}

fun providesApi(retrofit: Retrofit): RestApiServices {
    return retrofit.create(RestApiServices::class.java)
}

fun providesOltService(retrofit: Retrofit): SmartOltApiService {
    return retrofit.create(SmartOltApiService::class.java)
}
