package com.dscorp.ispadmin.presentation.di.modules

import android.app.Application
import android.content.Context
import com.example.data2.data.repository.IRepository
import com.example.data2.data.repository.Repository
import org.koin.android.ext.koin.androidContext
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
    single<IRepository> { Repository(androidContext()) }
}



