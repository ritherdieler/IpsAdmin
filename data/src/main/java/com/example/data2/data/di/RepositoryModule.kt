package com.example.data2.data.di

import com.example.data2.data.repository.IOltRepository
import com.example.data2.data.repository.IRepository
import com.example.data2.data.repository.OltRepository
import com.example.data2.data.repository.Repository
import org.koin.dsl.module

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/

val repositoryModule = module {
    single<IRepository> { Repository() }
    single<IOltRepository> { OltRepository(get()) }

}
