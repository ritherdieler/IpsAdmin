package com.dscorp.ispadmin.presentation.di

import com.dscorp.ispadmin.domain.repository.IRepository
import com.dscorp.ispadmin.data.repository.Repository
import com.dscorp.ispadmin.data.api.RestApiServices
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
    single<IRepository> { Repository() }
}

fun providesApi(retrofit: Retrofit): RestApiServices {
    return retrofit.create(RestApiServices::class.java)
}



