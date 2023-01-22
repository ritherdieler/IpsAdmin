package com.dscorp.ispadmin.presentation.di

import com.example.data2.data.repository.IRepository
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
    single<IRepository> { com.example.data2.data.repository.Repository() }
}



