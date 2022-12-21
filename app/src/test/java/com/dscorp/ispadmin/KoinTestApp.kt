package com.dscorp.ispadmin

import android.app.Application
import com.dscorp.ispadmin.di.repositoryModule
import com.dscorp.ispadmin.di.viewModelModule
import org.koin.core.context.startKoin

/**
 * Created by Sergio Carrillo Diestra on 21/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class KoinTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(viewModelModule, repositoryModule)
        }
    }
}