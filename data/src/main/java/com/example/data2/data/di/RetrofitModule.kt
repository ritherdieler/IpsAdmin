package com.example.data2.data.di

import com.example.data2.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "BASE_URL"

val retrofitModule = module {
    single { provideRetrofit(getProperty(BASE_URL), provideHttpClient()) }
    single { provideHttpClient() }
}

fun provideRetrofit(url: String, okHttpClient: OkHttpClient): Retrofit {
    val gson = GsonBuilder().create()

    return Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
}

fun provideHttpClient(): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    httpClient.addInterceptor(logging)
    if (BuildConfig.DEBUG) {
        httpClient.addNetworkInterceptor(StethoInterceptor())
    }

    return httpClient.build()
}

fun provideOltServiceHttpClient(): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    httpClient.addInterceptor(logging)
    if (BuildConfig.DEBUG) {
        httpClient.addNetworkInterceptor(StethoInterceptor())
    }
    httpClient.addInterceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("X-Token", BuildConfig.OLT_SERVICE_API_KET)
            .header("X-Token", BuildConfig.OLT_SERVICE_API_KET)
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    return httpClient.build()
}
