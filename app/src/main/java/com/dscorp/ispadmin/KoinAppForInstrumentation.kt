package com.dscorp.ispadmin

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

/**
 * Created by Sergio Carrillo Diestra on 25/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 */
class KoinAppForInstrumentation : Application() {



    override fun onCreate() {
        super.onCreate()
        startKoin {
//            androidLogger()
//            androidContext(this@KoinAppForInstrumentation)
            modules(emptyList())
        }
    }

    internal fun loadModules(module: Module, block: () -> Unit) {
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }

}