package com.dscorp.ispadmin.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/


@Module
@InstallIn(SingletonComponent::class)
class RetroFitModule {

@Provides
@Singleton
    fun providesRetrofit(): Retrofit {
    val gson = GsonBuilder().create()
    val httpClient = OkHttpClient.Builder()
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl("https://ispadmin.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
    }

    @Provides
    @Singleton
    fun providesApi(retrofit :Retrofit):RestApiServices{
        return retrofit.create(RestApiServices::class.java)
    }

    @Provides
    @Singleton
    fun providesRepository(restApiServices: RestApiServices): Repository {
        return Repository(restApiServices)
    }

}