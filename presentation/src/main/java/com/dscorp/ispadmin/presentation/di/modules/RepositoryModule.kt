package com.dscorp.ispadmin.presentation.di.modules

import com.example.data2.data.repository.IRepository
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
}
