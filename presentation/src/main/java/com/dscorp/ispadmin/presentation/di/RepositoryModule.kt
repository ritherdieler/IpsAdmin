package com.dscorp.ispadmin.presentation.di

import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.data2.data.repository.Repository
import com.example.data2.data.api.RestApiServices
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/


val repositoryModule = module {
    single { providesApi(get()) }
    single<IRepository> { com.example.data2.data.repository.Repository() }
}

fun providesApi(retrofit: Retrofit): com.example.data2.data.api.RestApiServices {
    return retrofit.create(com.example.data2.data.api.RestApiServices::class.java)
}


