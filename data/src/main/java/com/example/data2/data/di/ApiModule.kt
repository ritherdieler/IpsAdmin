package com.example.data2.data.di

import com.example.data2.BuildConfig
import com.example.data2.data.api.RestApiServices
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single { providesApi(get()) }

}

fun providesApi(retrofit: Retrofit): RestApiServices {
    return retrofit.create(RestApiServices::class.java)
}

