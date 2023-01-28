package com.dscorp.ispadmin.TestApp

import android.app.Application
import com.dscorp.ispadmin.presentation.di.modules.apiModule
import com.dscorp.ispadmin.presentation.di.repositoryModule
import com.dscorp.ispadmin.presentation.di.viewModelModule
import com.example.data2.data.di.BASE_URL
import com.example.data2.data.di.retrofitModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.koin.android.ext.android.getKoin
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
    private val FAKE_BASE_URL = "http://127.0.0.1:8081"

    override fun onCreate() = runTest {
        super.onCreate()

        launch {
            startKoin {
                getKoin().run {
                    setProperty(BASE_URL, FAKE_BASE_URL)
                }

                modules(
                    listOf(
                        apiModule,
                        retrofitModule,
                        viewModelModule,
                        repositoryModule
                    )
                )

            }
        }


    }


    internal fun loadModules(module: Module, block: () -> Unit) {
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }

}